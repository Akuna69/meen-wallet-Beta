package testutils

import (
	"encoding/hex"

	"github.com/btcsuite/btcd/btcec/v2"

	"github.com/meen/libwallet/cryptography/bitcoin_hpke"
	"github.com/meen/libwallet/domain/model/encrypted_key_v3"
	"github.com/meen/libwallet/service/model"
)

// BuildVerifiableMeenKeyJson creates a valid VerifiableMeenKeyJson for testing.
// The meen key is split into two halves: firstHalf encrypted to the user's public key,
// secondHalf encrypted to the recovery code's public key. If withProof is true, includes
// "mock_proof" which bypasses ZK verification in test mode.
func BuildVerifiableMeenKeyJson( //nolint:staticcheck // TODO: func BuildVerifiableMeenKeyJson should be BuildVerifiableMeenKeyJSON
	testKeys *TestKeys,
	withProof bool,
) *model.VerifiableMeenKeyJson {
	// Split the meen private key: meenPrivKey = firstHalf + secondHalf
	firstHalfKey, err := btcec.NewPrivateKey()
	if err != nil {
		panic("failed to generate first half key: " + err.Error())
	}

	meenECPrivateKey, err := testKeys.MeenKey.ECPrivateKey()
	if err != nil {
		panic("failed to get meen EC private key: " + err.Error())
	}

	secondHalfKeyBytes := new(btcec.ModNScalar).
		Set(&firstHalfKey.Key).
		Negate().
		Add(&meenECPrivateKey.Key).
		Bytes()

	// Encrypt first half to user's public key (this is what Verify() will decrypt)
	userECPubKey, err := testKeys.UserKey.PublicKey().ECPubKey()
	if err != nil {
		panic("failed to get user EC public key: " + err.Error())
	}

	firstHalfEncToClient, err := bitcoin_hpke.SingleShotEncrypt(
		firstHalfKey.Serialize(),
		userECPubKey,
		[]byte(encrypted_key_v3.MeenFirstHalfToClient),
		[]byte(""),
	)
	if err != nil {
		panic("failed to encrypt first half to client: " + err.Error())
	}

	// Encrypt second half to recovery code's public key
	rcPubKey := testKeys.RecoveryCodeKey.PubKey()

	secondHalfEncToRC, err := bitcoin_hpke.SingleShotEncrypt(
		secondHalfKeyBytes[:],
		rcPubKey,
		[]byte(encrypted_key_v3.MeenSecondHalfToRecoveryCode),
		[]byte(""),
	)
	if err != nil {
		panic("failed to encrypt second half to recovery code: " + err.Error())
	}

	var proof *string
	if withProof {
		p := "mock_proof"
		proof = &p
	}

	return &model.VerifiableMeenKeyJson{
		FirstHalfKeyEncryptedToClient:        hex.EncodeToString(firstHalfEncToClient.Serialize()),
		SecondHalfKeyEncryptedToRecoveryCode: hex.EncodeToString(secondHalfEncToRC.Serialize()),
		Proof:                                proof,
	}
}

// BuildInvalidVerifiableMeenKeyJson returns a VerifiableMeenKeyJson with invalid hex data
// that will cause parsing to fail inside ComputeAndStoreEncryptedMeenKeyAction.
func BuildInvalidVerifiableMeenKeyJson() model.VerifiableMeenKeyJson { //nolint:staticcheck // TODO: func BuildInvalidVerifiableMeenKeyJson should be BuildInvalidVerifiableMeenKeyJSON
	return model.VerifiableMeenKeyJson{
		FirstHalfKeyEncryptedToClient:        "not-valid-hex",
		SecondHalfKeyEncryptedToRecoveryCode: "not-valid-hex",
		Proof:                                nil,
	}
}
