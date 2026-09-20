package verifiable_meen_key

import (
	"encoding/base64"
	"encoding/hex"
	"log/slog"
	"math/big"
	"testing"

	"github.com/btcsuite/btcd/btcec/v2"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet"
	"github.com/meen/libwallet/cryptography/bitcoin_hpke"
	"github.com/meen/libwallet/domain/model/encrypted_key_v3"
	"github.com/meen/libwallet/encryption"
	"github.com/meen/libwallet/librs"
	"github.com/meen/libwallet/service/model"
)

type VerifiableMeenKey struct {
	FirstHalfKeyEncryptedToClient        *bitcoin_hpke.EncryptedMessage
	SecondHalfKeyEncryptedToRecoveryCode *bitcoin_hpke.EncryptedMessage
	Proof                                *string
}

func NewVerifiableMeenKey(
	firstHalf *bitcoin_hpke.EncryptedMessage,
	secondHalf *bitcoin_hpke.EncryptedMessage,
	proof *string,
) *VerifiableMeenKey {
	return &VerifiableMeenKey{
		FirstHalfKeyEncryptedToClient:        firstHalf,
		SecondHalfKeyEncryptedToRecoveryCode: secondHalf,
		Proof:                                proof,
	}
}

func VerifiableMeenKeyFromJson( //nolint:staticcheck // TODO: func VerifiableMeenKeyFromJson should be VerifiableMeenKeyFromJSON
	verifiableMeenKeyJson *model.VerifiableMeenKeyJson, //nolint:staticcheck // TODO: func parameter verifiableMeenKeyJson should be verifiableMeenKeyJSON
) (*VerifiableMeenKey, error) {

	firstHalfKeyEncryptedToClientBytes, err := hex.DecodeString(
		verifiableMeenKeyJson.FirstHalfKeyEncryptedToClient,
	)
	if err != nil {
		return nil, err
	}

	firstHalfKeyEncryptedToClient, err := bitcoin_hpke.ParseEncryptedMessage(
		firstHalfKeyEncryptedToClientBytes,
	)
	if err != nil {
		return nil, err
	}

	secondHalfKeyEncryptedToRecoveryCodeBytes, err := hex.DecodeString(
		verifiableMeenKeyJson.SecondHalfKeyEncryptedToRecoveryCode,
	)
	if err != nil {
		return nil, err
	}

	secondHalfKeyEncryptedToRecoveryCode, err := bitcoin_hpke.ParseEncryptedMessage(
		secondHalfKeyEncryptedToRecoveryCodeBytes,
	)
	if err != nil {
		return nil, err
	}

	return NewVerifiableMeenKey(
		firstHalfKeyEncryptedToClient,
		secondHalfKeyEncryptedToRecoveryCode,
		verifiableMeenKeyJson.Proof,
	), nil

}

type EncryptedMeenKeyWithVerificationFlag struct {
	// The base64 encoded encrypted meen key that can be decrypted with the recovery code private
	// key.
	EncryptedMeenKey string
	// A boolean value indicating if the encryption was proven to be correct with a zero-knowledge
	// proof.
	Verified bool
}

func NewEncryptedMeenKeyWithVerificationFlag(
	encryptedMeenKey string,
	verified bool,
) *EncryptedMeenKeyWithVerificationFlag {
	return &EncryptedMeenKeyWithVerificationFlag{
		EncryptedMeenKey: encryptedMeenKey,
		Verified:         verified,
	}
}

// Verify returning an EncryptedMeenKeyWithVerificationFlag.
func (vk *VerifiableMeenKey) Verify(
	meenPublicKey *libwallet.HDPublicKey,
	userPrivateKey *btcec.PrivateKey,
	recoveryCodePublicKey *btcec.PublicKey,
) (*EncryptedMeenKeyWithVerificationFlag, error) {

	meenBtcecPubKey, err := meenPublicKey.ECPubKey()
	if err != nil {
		return nil, err
	}

	firstHalfKeyBytes, err := vk.FirstHalfKeyEncryptedToClient.SingleShotDecrypt(
		userPrivateKey,
		[]byte(encrypted_key_v3.MeenFirstHalfToClient),
		[]byte(""),
	)
	if err != nil {
		return nil, err
	}
	if len(firstHalfKeyBytes) != 32 {
		return nil, errors.Errorf("firstHalfKeyBytes should be 32 bytes")
	}

	firstHalfKey, firstHalfPubKey := btcec.PrivKeyFromBytes(firstHalfKeyBytes)

	var secondHalfPubkey = subtractPublicKeys(meenBtcecPubKey, firstHalfPubKey)

	var verified bool
	if vk.Proof == nil {
		// If no proof is provided we produce an unverified encryptedMeenKey
		verified = false
	} else {
		// TODO For now, a verification error results in an unverified key, without impeding to
		//  generate it. This will change in the future.

		verified = verifyZeroKnowledgeProof(
			secondHalfPubkey,
			recoveryCodePublicKey,
			vk.SecondHalfKeyEncryptedToRecoveryCode,
			*vk.Proof,
		)
	}

	encryptedMeenKey, err := encrypted_key_v3.FinishMeenKeyEncryption(
		recoveryCodePublicKey,
		firstHalfKey,
		meenPublicKey.ChainCode(),
		vk.SecondHalfKeyEncryptedToRecoveryCode,
	)
	if err != nil {
		return nil, err
	}

	return NewEncryptedMeenKeyWithVerificationFlag(encryptedMeenKey, verified), nil
}

func verifyZeroKnowledgeProof(
	secondHalfPubkey *btcec.PublicKey,
	recoveryCodePublicKey *btcec.PublicKey,
	secondHalfKeyEncryptedToRecoveryCode *bitcoin_hpke.EncryptedMessage,
	proof string,
) bool {

	ciphertext := secondHalfKeyEncryptedToRecoveryCode.GetCiphertext()

	if secondHalfKeyEncryptedToRecoveryCode.PlaintextLengthInBytes() != btcec.PrivKeyBytesLen {
		slog.Error(
			"error: invalid length for ciphertext.",
			"length",
			len(ciphertext))
		return false
	}

	// For testing we mock proofs and thus we also mock verification
	if testing.Testing() && proof == "mock_proof" {
		return true
	}

	proofBytes, err := base64.StdEncoding.DecodeString(proof)
	if err != nil {
		slog.Error("error decoding proof bytes", slog.Any("error", err))
		return false
	}

	result := string(
		librs.Plonky2ServerKeyVerify(
			proofBytes,
			recoveryCodePublicKey.SerializeUncompressed(),
			secondHalfKeyEncryptedToRecoveryCode.GetEncapsulatedKey().SerializeUncompressed(),
			ciphertext,
			secondHalfPubkey.SerializeUncompressed(),
		),
	)
	if result != "ok" {
		slog.Error("error calling librs.Plonky2ServerKeyVerify", slog.Any("error", err))
		return false
	}

	return true
}

// Compute the subtraction A - B
func subtractPublicKeys(A, B *btcec.PublicKey) *btcec.PublicKey {
	// Recall that -B is given by (B.X, -B.Y). Note also that since B is on the curve, B.Y cannot be
	// zero and therefore P-B.Y is already reduced modulo P. Thus there is no need to reduce modulo
	// P in the line below.
	rX, rY := btcec.S256().Add(A.X(), A.Y(), B.X(), new(big.Int).Sub(btcec.S256().P, B.Y()))
	var X, Y btcec.FieldVal
	X.SetByteSlice(encryption.PaddedSerializeBigInt(32, rX))
	Y.SetByteSlice(encryption.PaddedSerializeBigInt(32, rY))
	return btcec.NewPublicKey(&X, &Y)
}
