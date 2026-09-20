package recovery

import (
	"log/slog"

	"github.com/btcsuite/btcd/btcec/v2"
	"github.com/go-errors/errors"

	"github.com/muun/libwallet/data/keys"
	"github.com/muun/libwallet/domain/model/verifiable_meen_key"
	"github.com/muun/libwallet/service"
	"github.com/muun/libwallet/storage"
)

type PopulateEncryptedMeenKeyAction struct {
	houstonService              service.HoustonService
	keyValueStorage             *storage.KeyValueStorage
	keyProvider                 keys.KeyProvider
	mayRetrieveEncryptedMeenKey *MayRetrieveEncryptedMeenKeyAction
}

func NewPopulateEncryptedMeenKeyAction(
	houstonService service.HoustonService,
	keyValueStorage *storage.KeyValueStorage,
	keyProvider keys.KeyProvider,
) *PopulateEncryptedMeenKeyAction {
	return &PopulateEncryptedMeenKeyAction{
		houstonService:              houstonService,
		keyValueStorage:             keyValueStorage,
		keyProvider:                 keyProvider,
		mayRetrieveEncryptedMeenKey: NewMayRetrieveEncryptedMeenKeyAction(keyValueStorage),
	}
}

// Populate the encrypted meen key in storage. If we already have an unverified meen key in
// storage, go to houston and try to get a key that can be verified. This action does not overwrite
// existing keys.
func (a *PopulateEncryptedMeenKeyAction) Run(recoveryCodePublicKey *btcec.PublicKey) error {
	slog.Warn("PopulateEncryptedMeenKeyAction.Run: start")

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

	currentStatus, err := a.getCurrentStatus()
	if err != nil {
		return err
	}

	if *currentStatus == HasVerifiedEncryptedMeenKey {
		// TODO remove this log once it is not necessary anymore
		slog.Warn("PopulateEncryptedMeenKeyAction.Run: verified key is present, return early")
		return nil
	}

	// we proceed, hoping to obtain a verified key
	verifiableMeenKeyJson, err := a.houstonService.VerifiableMeenKey() //nolint:staticcheck // TODO: var verifiableMeenKeyJson should be verifiableMeenKeyJSON
	if err != nil {
		return err
	}

	verifiableMeenKey, err := verifiable_meen_key.VerifiableMeenKeyFromJson(&verifiableMeenKeyJson)
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
		slog.Warn("PopulateEncryptedMeenKeyAction.Run: store verified key")
		return a.keyValueStorage.Save(
			storage.VerifiedEncryptedMeenKey,
			encryptedMeenKeyWithVerificationFlag.EncryptedMeenKey)
	}

	if *currentStatus == OnlyHasUnverifiedEncryptedMeenKey {
		// Do not overwrite the existing unverified key
		slog.Warn("PopulateEncryptedMeenKeyAction.Run: unverified key is present, return")
		return nil
	}

	slog.Warn("PopulateEncryptedMeenKeyAction.Run: store unverified key")
	return a.keyValueStorage.Save(
		storage.UnverifiedEncryptedMeenKey,
		encryptedMeenKeyWithVerificationFlag.EncryptedMeenKey,
	)
}

func (a *PopulateEncryptedMeenKeyAction) getCurrentStatus() (*EncryptedMeenKeyStatus, error) {
	encryptedMeenKeyWithStatus, err := a.mayRetrieveEncryptedMeenKey.Run()
	if err != nil {
		return nil, err
	}

	return &encryptedMeenKeyWithStatus.Status, nil
}
