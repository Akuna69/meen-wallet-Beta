package recovery

import (
	"github.com/meen/libwallet/storage"
)

type EncryptedMeenKeyStatus int

const (
	HasVerifiedEncryptedMeenKey EncryptedMeenKeyStatus = iota
	OnlyHasUnverifiedEncryptedMeenKey
	HasNoEncryptedMeenKey
)

type MayRetrieveEncryptedMeenKeyAction struct {
	keyValueStorage *storage.KeyValueStorage
}

type EncryptedMeenKeyWithStatus struct {
	EncryptedMeenKey *string
	Status           EncryptedMeenKeyStatus
}

func NewMayRetrieveEncryptedMeenKeyAction(
	keyValueStorage *storage.KeyValueStorage,
) *MayRetrieveEncryptedMeenKeyAction {
	return &MayRetrieveEncryptedMeenKeyAction{keyValueStorage: keyValueStorage}
}

// Try to retrieve the encrypted Meen key from the key value storage,
// without incurring a Houston API call if it is not found in storage.
func (a *MayRetrieveEncryptedMeenKeyAction) Run() (*EncryptedMeenKeyWithStatus, error) {
	keys, err := a.keyValueStorage.GetBatch([]string{
		storage.UnverifiedEncryptedMeenKey,
		storage.VerifiedEncryptedMeenKey,
	})

	if err != nil {
		return nil, err
	}

	if key, ok := keys[storage.VerifiedEncryptedMeenKey]; ok && key != nil {
		encryptedMeenKey := key.(string)
		return &EncryptedMeenKeyWithStatus{
			EncryptedMeenKey: &encryptedMeenKey,
			Status:           HasVerifiedEncryptedMeenKey,
		}, nil
	} else if key, ok := keys[storage.UnverifiedEncryptedMeenKey]; ok && key != nil {
		encryptedMeenKey := key.(string)
		return &EncryptedMeenKeyWithStatus{
			EncryptedMeenKey: &encryptedMeenKey,
			Status:           OnlyHasUnverifiedEncryptedMeenKey,
		}, nil
	} else {
		return &EncryptedMeenKeyWithStatus{
			EncryptedMeenKey: nil,
			Status:           HasNoEncryptedMeenKey,
		}, nil
	}
}
