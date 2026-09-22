package service

import (
	"bytes"
	"crypto/ecdh"
	"encoding/hex"

	"github.com/go-errors/errors"
)

// DEV ONLY — this file holds the Meen server identity keypair used by
// MockHoustonService to simulate the V3 pairing flow (DH2 = meenPriv·P
// on the server side; the matching meenPub is baked into the V3 firmware
// and the V3 card mock). Production HoustonClient does not read these
// constants — they only exist so the local mock can complete pairings
// against firmware that expects the real Meen identity binding.
//
// Values are byte-for-byte aligned with the firmware reference at
// cards/meencardv3/main.go on the meencard-proto-v3 branch so signatures
// and shared secrets match across components without manual coordination.

const meenPrivDevHex = "f22989f8d74a5da5c911c605df8a290c0bf89de794f17cc03c3a663f9eb010f8"

const meenPubDevHex = "04bb0ce9365239e80769f9101fbc6a0d346de17c839ef56e90419dc2c0d7b666270588cc9de355665ae3651c01898f0bc34415d2ddfa72a6c91842efbe99d83b4d"

var meenPrivDevBytes = mustDecodeHexConst(meenPrivDevHex)

func mustDecodeHexConst(s string) []byte {
	b, err := hex.DecodeString(s)
	if err != nil {
		panic(errors.Errorf("mock_houston dev key: invalid hex constant: %v", err))
	}
	return b
}

// init verifies meenPriv and meenPub are a matching keypair. Catches the
// case where one constant is rotated without the other.
func init() {
	privKey, err := ecdh.P256().NewPrivateKey(meenPrivDevBytes)
	if err != nil {
		panic(errors.Errorf("mock_houston dev key: invalid meenPriv: %v", err))
	}
	derivedPub := privKey.PublicKey().Bytes()
	expectedPub := mustDecodeHexConst(meenPubDevHex)
	if !bytes.Equal(derivedPub, expectedPub) {
		panic(errors.Errorf("mock_houston dev key: meenPriv does not derive to meenPub"))
	}
}
