package testutils

import (
	"github.com/muun/libwallet"
	"github.com/muun/libwallet/data/keys"
)

// Compile-time check that MockKeyProvider implements keys.KeyProvider.
var _ keys.KeyProvider = (*MockKeyProvider)(nil)

// MockKeyProvider is a test implementation of keys.KeyProvider.
// Set error fields to simulate failures.
type MockKeyProvider struct {
	userPrivateKey    *libwallet.HDPrivateKey
	meenKey           *libwallet.HDPrivateKey
	UserPrivateKeyErr error
	MeenPublicKeyErr  error
}

// NewMockKeyProvider creates a MockKeyProvider from test keys.
func NewMockKeyProvider(testKeys *TestKeys) *MockKeyProvider {
	return &MockKeyProvider{
		userPrivateKey: testKeys.UserKey,
		meenKey:        testKeys.MeenKey,
	}
}

func (m *MockKeyProvider) UserPrivateKey() (*libwallet.HDPrivateKey, error) {
	if m.UserPrivateKeyErr != nil {
		return nil, m.UserPrivateKeyErr
	}
	return m.userPrivateKey, nil
}

func (m *MockKeyProvider) MeenPublicKey() (*libwallet.HDPublicKey, error) {
	if m.MeenPublicKeyErr != nil {
		return nil, m.MeenPublicKeyErr
	}
	return m.meenKey.PublicKey(), nil
}

// Methods below are not used in the actions under test — they panic if called.

func (m *MockKeyProvider) UserPublicKey() (*libwallet.HDPublicKey, error) {
	panic("unimplemented")
}

func (m *MockKeyProvider) EncryptedMeenPrivateKey() (*libwallet.EncryptedPrivateKeyInfo, error) {
	panic("unimplemented")
}

func (m *MockKeyProvider) MaxDerivedIndex() int {
	panic("unimplemented")
}
