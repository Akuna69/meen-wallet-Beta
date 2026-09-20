package recovery

import (
	"log/slog"

	"github.com/btcsuite/btcd/btcec/v2"
	"github.com/go-errors/errors"

	"github.com/muun/libwallet/data/keys"
	"github.com/muun/libwallet/domain/model/verifiable_meen_key"
	"github.com/muun/libwallet/service/model"
	"github.com/muun/libwallet/storage"
)

type ComputeAndStoreEncryptedMeenKeyAction struct {
	keyValueStorage *storage.KeyValueStorage
	keyProvider     keys.KeyProvider
}

func NewComputeAndStoreEncryptedMeenKeyAction(
	keyValueStorage *storage.KeyValueStorage,
	keyProvider keys.KeyProvider,
) *ComputeAndStoreEncryptedMeenKeyAction {
	return &ComputeAndStoreEncryptedMeenKeyAction{
		keyValueStorage: keyValueStorage,
		keyProvider:     keyProvider,
	}
}

// Verify and store the resulting encrypted meen key. This action overwrites existing keys.
func (a *ComputeAndStoreEncryptedMeenKeyAction) Run(
	recoveryCodePublicKey *btcec.PublicKey,
	verifiableMeenKeyJson *model.VerifiableMeenKeyJson, //nolint:staticcheck // TODO: method parameter verifiableMeenKeyJson should be verifiableMeenKeyJSON
) error {
	slog.Warn("ComputeAndStoreEncryptedMeenKeyAction.Run: start")

	userHDPrivateKey, err := a.keyProvider.UserPrivateKey()
	if err != nil {
		return errors.Errorf("error getting user key from KeyProvider: %w", err)
	}

	userEcPrivateKey, err := userHDPrivateKey.ECPrivateKey()
	if err != nil {
		return errors.Errorf("error obtaining user ec private key: %w", err)
	}

	meenHDPublicKey, err := a.keyProvider.MeenPublicKey()
	if err != nil {
		return errors.Errorf("error obtaining meen key from KeyProvider: %w", err)
	}

	verifiableMeenKey, err := verifiable_meen_key.VerifiableMeenKeyFromJson(verifiableMeenKeyJson)
	if err != nil {
		return err
	}

	encryptedMeenKeyWithVerificationFlag, err := verifiableMeenKey.Verify(
		meenHDPublicKey,
		userEcPrivateKey,
		recoveryCodePublicKey,
	)
	if err != nil {
		return err
	}

	if encryptedMeenKeyWithVerificationFlag.Verified {
		slog.Warn("ComputeAndStoreEncryptedMeenKeyAction.Run: store verified key")

		return a.keyValueStorage.Save(
			storage.VerifiedEncryptedMeenKey,
			encryptedMeenKeyWithVerificationFlag.EncryptedMeenKey)
	}

	slog.Warn("ComputeAndStoreEncryptedMeenKeyAction.Run: store unverified key")

	return a.keyValueStorage.Save(
		storage.UnverifiedEncryptedMeenKey,
		encryptedMeenKeyWithVerificationFlag.EncryptedMeenKey,
	)
}
