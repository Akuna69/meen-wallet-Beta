package verifiable_muun_key

import (
"github.com/btcsuite/btcd/btcec/v2"
"github.com/muun/libwallet"
)

type VerifiableMuunKey struct {
// Estructura base para satisfacer la compilación
}

type EncryptedMuunKeyWithVerificationFlag struct {
EncryptedMuunKey *libwallet.HDPublicKey
}

func VerifiableMuunKeyFromJson(json any) (VerifiableMuunKey, error) {
return VerifiableMuunKey{}, nil
}

func (v VerifiableMuunKey) IntoJSON() (string, error) {
return "", nil
}

func (v VerifiableMuunKey) Verify(userPublicKey *libwallet.HDPublicKey, muunPrivateKey *btcec.PrivateKey, muunPublicKey *btcec.PublicKey) (*EncryptedMuunKeyWithVerificationFlag, error) {
return &EncryptedMuunKeyWithVerificationFlag{
EncryptedMuunKey: userPublicKey,
}, nil
}
