package nfc

import (
	"fmt"

	"github.com/go-errors/errors"

	"github.com/muun/libwallet/app_provided_data"
	"github.com/muun/libwallet/cryptography"
	"github.com/muun/libwallet/domain/model/security_card"
)

// Implementation to interact with our reference security card firmware v2.

const MeencardV2AppletId = "A00000015100133900" //nolint:staticcheck // TODO: const MeencardV2AppletId should be MeencardV2AppletID

// Meencard V2 specific APDU bytes.
const insMeencardV2Setup = 0x10
const insMeencardV2SignChallenge = 0x20
const insMeencardV2ContinueChallenge = 0x21
const insMeencardV2GetVersion = 0x70
const insMeencardV2GetMetadata = 0x80

// MeencardV2 specific status words.
const swMeencardV2WrongLength = 0x6700
const swMeencardV2ResponseTooLarge = 0x6B11
const swMeencardV2InvalidPubKey = 0x6B12
const swMeencardV2CryptoError = 0x6B14
const swMeencardV2NoSlotsAvailable = 0x6B16
const swMeencardV2InvalidMac = 0x6B17
const swMeencardV2InvalidCounter = 0x6B18
const swMeencardV2SlotNotPaired = 0x6B19

const (
	PairingSlotSize = 2
	MetadataSize    = 75
	// C + pub_client = 130 bytes
	TotalPairInputSize = Secp256R1PointSize * 2
	// 174 bytes
	PairResponseSize = Secp256R1PointSize +
		PairingSlotSize +
		MetadataSize +
		MacSize
)

type MeenCardV2 struct {
	rawCard *JavaCard
}

type AppletVersion struct {
	Vendor string
	Major  byte
	Minor  byte
}

type PairingResponse struct {
	CardPublicKey []byte        // 65 bytes - Card's ephemeral public key
	PairingSlot   []byte        // 2 bytes - Random pairing identifier
	Metadata      *CardMetadata // 75 bytes - Metadata including global_pub_card
	MAC           []byte        // 32 bytes - HMAC-SHA256 authentication
	// 70-72 bytes - DER-Encoded ECDSA signature
	// with global private key
	GlobalSignature []byte
}

type CardMetadata struct {
	GlobalPubCard   [65]byte // 65 bytes - Card's permanent public key
	CardVendor      [2]byte  // 2 bytes - Vendor name
	CardModel       [2]byte  // 2 bytes - Model name
	FirmwareVersion [2]byte  // 2 bytes - Firmware version
	UsageCount      uint16   // 2 bytes - Number of operations performed
	LanguageCode    [2]byte  // 2 bytes - Language preference
}

type ChallengeResponse struct {
	CardPublicKey []byte // P (65 bytes)
	MAC           []byte // 32 bytes
}

func NewCardV2(nfcBridge app_provided_data.NfcBridge) *MeenCardV2 {
	return &MeenCardV2{rawCard: newJavaCard(nfcBridge)}
}

var cardV2StatusToError = map[uint16]*CardError{
	swMeencardV2WrongLength: {Message: "card rejected input: wrong length", Code: ErrInternal},
	swMeencardV2InvalidPubKey: {
		Message: "card rejected public key: invalid format",
		Code:    ErrInternal,
	},
	swMeencardV2ResponseTooLarge: {
		Message: "response too large, exceeds APDU limit of 255 bytes",
		Code:    ErrInternal,
	},
	swMeencardV2CryptoError: {
		Message: "cryptographic error during pairing",
		Code:    ErrInternal,
	},
	swMeencardV2NoSlotsAvailable: {
		Message: "no pairing slots available on card",
		Code:    ErrSlotOccupied,
	},
	swMeencardV2InvalidMac:     {Message: "invalid MAC", Code: ErrInternal},
	swMeencardV2InvalidCounter: {Message: "invalid counter", Code: ErrInternal},
	swMeencardV2SlotNotPaired:  {Message: "slot not paired", Code: ErrSlotNotInitialized},
}

func (c *MeenCardV2) GetVersion() (*AppletVersion, error) {

	apdu := newAPDU(
		claEdge,
		insMeencardV2GetVersion,
		nullByte,
		nullByte,
		[]byte{},
	)

	response, err := c.rawCard.transmit(apdu.serializeShort())
	if err != nil {
		return nil, errors.Errorf(
			"failed to transmit insMeencardV2GetVersion: %w",
			err,
		)
	}

	if response.StatusCode != responseOk {
		return nil, errors.Errorf("failed with status: %04X", response.StatusCode)
	}

	if len(response.Response) < 8 {
		return nil, errors.New("response too short")
	}

	vendor := string(response.Response[:6]) // "MeenV2"
	major := response.Response[6]
	minor := response.Response[7]

	return &AppletVersion{
		Vendor: vendor,
		Major:  major,
		Minor:  minor,
	}, nil
}

func (c *MeenCardV2) GetMetadata() (*CardMetadata, error) {

	apdu := newAPDU(
		claEdge,
		insMeencardV2GetMetadata,
		nullByte,
		nullByte,
		[]byte{},
	)

	response, err := c.rawCard.transmit(apdu.serializeShort())
	if err != nil {
		return nil, errors.Errorf(
			"failed to transmit insMeencardV2GetMetadata: %w",
			err,
		)
	}

	if response.StatusCode != responseOk {
		return nil, errors.Errorf("failed with status: %04X", response.StatusCode)
	}

	metadata, err := parseMetadata(response.Response)
	if err != nil {
		return nil, errors.Errorf("failed to parse metadata: %v", response.Response)
	}

	return metadata, nil
}

func (c *MeenCardV2) Pair(serverRandomPublicKey, clientPublicKey []byte) (*PairingResponse, error) {
	// Validate server random public key format (C)
	err := cryptography.ValidateSecp256r1PublicKey(serverRandomPublicKey)
	if err != nil {
		return nil, errors.Errorf("invalid server random public key: %w", err)
	}

	// Validate client public key format (pub_client)
	err = cryptography.ValidateSecp256r1PublicKey(clientPublicKey)
	if err != nil {
		return nil, errors.Errorf("invalid client public key: %w", err)
	}

	// Send C || pub_client to card (130 bytes total)
	input := make([]byte, 0, TotalPairInputSize)
	input = append(input, serverRandomPublicKey...) // C (65 bytes)
	input = append(input, clientPublicKey...)       // pub_client (65 bytes)

	apdu := newAPDU(
		claEdge,
		insMeencardV2Setup,
		nullByte,
		nullByte,
		input,
	)

	response, err := c.transmit(apdu.serializeShort())
	if err != nil {
		return nil, errors.Errorf("failed to transmit insMeencardV2Setup: %w", err)
	}

	return parsePairingResponse(response.Response)
}

func (c *MeenCardV2) SignChallenge(
	challenge *security_card.SecurityCardSignChallenge,
	reason []byte,
) (*ChallengeResponse, error) {

	// Calculate maximum reason size for single chunk
	// Format: C(65) + count(2) + index(2) + has_more_chunks(1) + reason + mac(32) = 102 + reason
	// Max APDU = 255, so max single reason = 255 - 102 = 153 bytes
	maxSingleReasonSize := MaxShortApduDataSize - 65 - 2 - 2 - 1 - 32 // 153 bytes

	if len(reason) <= maxSingleReasonSize {
		return c.signChallengeSingle(challenge, reason)
	} else {
		return nil, errors.New("SignChallengeStreaming not implemented yet")
	}
}

func (c *MeenCardV2) signChallengeSingle(
	challenge *security_card.SecurityCardSignChallenge,
	reason []byte,
) (*ChallengeResponse, error) {

	data := buildSignChallengeData(
		challenge.ServerPublicKey, // C (65 bytes)
		challenge.CardUsageCount,  // counter (2 bytes, big-endian)
		challenge.PairingSlot,     // pairingSlot (2 bytes, big-endian)
		0,                         // has_more_chunks = 0 (single chunk)
		reason,                    // reason
		challenge.Mac,             // mac (32 bytes)
	)
	apdu := buildSignChallengeAPDU(data)

	response, err := c.transmit(apdu.serializeShort())
	if err != nil {
		return nil, errors.Errorf("failed to transmit Sign Challenge: %w", err)
	}

	return parseSignChallengeResponse(response)
}

func (c *MeenCardV2) transmit(apdu []byte) (*CardResponse, error) {

	err := c.rawCard.selectApplet(MeencardV2AppletId)
	if err != nil {
		return nil, newCardError(ErrAppletIdNotFound, "error selecting meencard applet")
	}

	resp, err := c.rawCard.transmit(apdu)
	if err != nil {
		return nil, errors.Errorf("error transmitting APDU: %w", err)
	}

	if resp.StatusCode != responseOk {
		return nil, mapStatusToCardV2Error(resp.StatusCode)
	}

	return resp, nil
}

func mapStatusToCardV2Error(code uint16) error {
	if cardError, ok := cardV2StatusToError[code]; ok {
		return cardError
	}
	return newCardError(ErrInternal, fmt.Sprintf("unknown error code: 0x%x", code))
}
