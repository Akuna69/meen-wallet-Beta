package keys

import (
	"github.com/go-errors/errors"

	"github.com/muun/libwallet"
	"github.com/muun/libwallet/app_provided_data"
)

// Provide keys. All keys are already derived at our usual base path "m/schema:1'/recovery:1'"
type KeyProvider interface {
	UserPrivateKey() (*libwallet.HDPrivateKey, error)
	UserPublicKey() (*libwallet.HDPublicKey, error)
	MeenPublicKey() (*libwallet.HDPublicKey, error)
	EncryptedMeenPrivateKey() (*libwallet.EncryptedPrivateKeyInfo, error)
	MaxDerivedIndex() int
}

type keyProvider struct {
	keyProvider app_provided_data.KeyProvider
	network     libwallet.Network
}

func NewKeyProvider(k app_provided_data.KeyProvider, network libwallet.Network) KeyProvider {
	return &keyProvider{keyProvider: k, network: network}
}

func (p *keyProvider) UserPrivateKey() (*libwallet.HDPrivateKey, error) {
	userKeyData, err := p.keyProvider.FetchUserKey()
	if err != nil {
		return nil, err
	}

	userPrivKey, err := libwallet.NewHDPrivateKeyFromString(
		userKeyData.Serialized,
		userKeyData.Path,
		&p.network,
	)
	if err != nil {
		return nil, err
	}

	return userPrivKey, nil
}

func (p *keyProvider) UserPublicKey() (*libwallet.HDPublicKey, error) {
	userPrivKey, err := p.UserPrivateKey()
	if err != nil {
		return nil, err
	}

	return userPrivKey.PublicKey(), nil
}

func (p *keyProvider) MeenPublicKey() (*libwallet.HDPublicKey, error) {
	meenKeyData, err := p.keyProvider.FetchMeenKey()
	if err != nil {
		return nil, err
	}

	meenKey, err := libwallet.NewHDPublicKeyFromString(
		meenKeyData.Serialized,
		meenKeyData.Path,
		&p.network,
	)
	if err != nil {
		return nil, err
	}

	return meenKey, nil
}

func (p *keyProvider) EncryptedMeenPrivateKey() (*libwallet.EncryptedPrivateKeyInfo, error) {
	encodedKeyData, err := p.keyProvider.FetchEncryptedMeenPrivateKey()
	if err != nil {
		return nil, err
	}

	return libwallet.DecodeEncryptedPrivateKey(encodedKeyData)
}

func (p *keyProvider) DecryptMeenPrivateKey(
	recoveryCode string,
	encryptedKey *libwallet.EncryptedPrivateKeyInfo,
	network *libwallet.Network,
) (*libwallet.DecryptedPrivateKey, error) {
	salt := encryptedKey.Salt
	decryptionKey, err := libwallet.RecoveryCodeToKey(recoveryCode, salt)

	if err != nil {
		return nil, errors.Errorf("failed to process recovery code: %w", err)
	}

	decryptedKey, err := decryptionKey.DecryptKey(encryptedKey, network)
	if err != nil {
		return nil, errors.Errorf("failed to decrypt ke: %w", err)
	}

	return decryptedKey, nil
}

func (p *keyProvider) MaxDerivedIndex() int {
	return p.keyProvider.FetchMaxDerivedIndex()
}
