package testutils

import (
	"crypto/rand"

	"github.com/btcsuite/btcd/btcec/v2"

	"github.com/meen/libwallet"
	"github.com/meen/libwallet/recoverycode"
)

// TestKeys holds all cryptographic keys needed for testing recovery and challenge key actions.
type TestKeys struct {
	UserKey         *libwallet.HDPrivateKey
	MeenKey         *libwallet.HDPrivateKey
	RecoveryCodeKey *btcec.PrivateKey
	RecoveryCode    string
}

// GenerateTestKeys creates a fresh set of test keys using Regtest network.
func GenerateTestKeys() *TestKeys {
	userKey, err := libwallet.NewHDPrivateKey(randomBytes(32), libwallet.Regtest())
	if err != nil {
		panic("failed to generate user key: " + err.Error())
	}

	meenKey, err := libwallet.NewHDPrivateKey(randomBytes(32), libwallet.Regtest())
	if err != nil {
		panic("failed to generate meen key: " + err.Error())
	}

	rc := recoverycode.Generate()
	rcKey, err := recoverycode.ConvertToKey(rc, "")
	if err != nil {
		panic("failed to convert recovery code to key: " + err.Error())
	}

	return &TestKeys{
		UserKey:         userKey,
		MeenKey:         meenKey,
		RecoveryCodeKey: rcKey,
		RecoveryCode:    rc,
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
