package verifiable_muun_key

import (
"github.com/btcsuite/btcd/btcec/v2"
"github.com/muun/libwallet"
"github.com/muun/libwallet/domain/model"
)

type VerifiableMuunKey struct {
// Estructura base para satisfacer la compilación
}

func VerifiableMuunKeyFromJson(json *model.VerifiableMuunKeyJson) (VerifiableMuunKey, error) {
return VerifiableMuunKey{}, nil
}

func (v VerifiableMuunKey) IntoJSON() (string, error) {
return "", nil
}

func (v VerifiableMuunKey) Verify(userPublicKey *libwallet.HDPublicKey, muunPrivateKey *btcec.PrivateKey, muunPublicKey *btcec.PublicKey) (*libwallet.HDPublicKey, error) {
return userPublicKey, nil
}
