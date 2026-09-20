package encrypted_key_v3

import (
	"bytes"
	"crypto/rand"
	"encoding/base64"
	"testing"

	"github.com/btcsuite/btcd/btcec/v2"

	"github.com/meen/libwallet"
	"github.com/meen/libwallet/cryptography/bitcoin_hpke"
	"github.com/meen/libwallet/recoverycode"
)

func TestFinishMeenKeyEncryption(t *testing.T) {

	recoveryCode := recoverycode.Generate()

	recoveryCodePrivateKey, err := recoverycode.ConvertToKey(recoveryCode, "")
	if err != nil {
		t.Fatal(err)
	}
	recoveryCodePublicKey := recoveryCodePrivateKey.PubKey()

	meenKey, err := libwallet.NewHDPrivateKey(randomBytes(32), libwallet.Mainnet())
	if err != nil {
		t.Fatal(err)
	}

	// simulate splitting Meen key and encrypting second half to RC

	firstHalfKey, err := btcec.NewPrivateKey()
	if err != nil {
		t.Fatal(err)
	}
	meenECPrivateKey, err := meenKey.ECPrivateKey()
	if err != nil {
		t.Fatal(err)
	}

	secondHalfKeyBytes := new(
		btcec.ModNScalar,
	).Set(&firstHalfKey.Key).
		Negate().
		Add(&meenECPrivateKey.Key).
		Bytes()
	secondHalfKeyEncryptedToRecoveryCode, err := bitcoin_hpke.SingleShotEncrypt(
		secondHalfKeyBytes[:],
		recoveryCodePublicKey,
		[]byte(MeenSecondHalfToRecoveryCode),
		[]byte(""),
	)
	if err != nil {
		t.Fatal(err)
	}

	// Now test FinishMeenKeyEncryption
	encryptedMeenKey, err := FinishMeenKeyEncryption(
		recoveryCodePublicKey,
		firstHalfKey,
		meenKey.ChainCode(),
		secondHalfKeyEncryptedToRecoveryCode,
	)
	if err != nil {
		t.Fatal(err)
	}

	decryptedMeenKey, err := DecryptExtendedKey(
		recoveryCodePrivateKey,
		encryptedMeenKey,
		libwallet.Mainnet(),
	)
	if err != nil {
		t.Fatal(err)
	}

	if !bytes.Equal(decryptedMeenKey.PublicKey().Raw(), meenKey.PublicKey().Raw()) {
		t.Fatal("decrypted public key does not match original public key")
	}

	if !bytes.Equal(decryptedMeenKey.ChainCode(), meenKey.ChainCode()) {
		t.Fatal("decrypted chain code does not match original chain code")
	}
}

func TestEncryptUserKey(t *testing.T) {

	recoveryCode := recoverycode.Generate()
	recoveryCodePrivateKey, err := recoverycode.ConvertToKey(recoveryCode, "")
	if err != nil {
		t.Fatal(err)
	}
	recoveryCodePublicKey := recoveryCodePrivateKey.PubKey()

	userKey, err := libwallet.NewHDPrivateKey(randomBytes(32), libwallet.Mainnet())
	if err != nil {
		t.Fatal(err)
	}

	encryptedUserKey, err := EncryptUserKey(userKey, recoveryCodePublicKey)
	if err != nil {
		t.Fatal(err)
	}

	decryptedUserKey, err := DecryptExtendedKey(
		recoveryCodePrivateKey,
		encryptedUserKey,
		libwallet.Mainnet(),
	)
	if err != nil {
		t.Fatal(err)
	}

	if !bytes.Equal(decryptedUserKey.PublicKey().Raw(), userKey.PublicKey().Raw()) {
		t.Fatal("decrypted public key does not match original public key")
	}

	if !bytes.Equal(decryptedUserKey.ChainCode(), userKey.ChainCode()) {
		t.Fatal("decrypted chain code does not match original chain code")
	}
}

func TestDeserializationFailsDueToWrongVersion(t *testing.T) {
	bs := []byte{2, 0, 1}
	key := base64.StdEncoding.EncodeToString(bs)
	_, err := deserializeEncryptedKeyV3(key)
	if err.Error() != "decrypting key: expected a v3 key, version byte indicates v2" {
		t.Fatal("Expected to fail due to unexpected meen key version")
	}
}

func randomBytes(count int) []byte {

	buf := make([]byte, count)
	_, err := rand.Read(buf)
	if err != nil {
		panic("couldn't read random bytes")
	}

	return buf
}
