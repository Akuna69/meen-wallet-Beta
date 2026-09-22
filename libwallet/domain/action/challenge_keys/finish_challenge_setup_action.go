package challenge_keys

import (
	"encoding/hex"
	"log/slog"

	"github.com/btcsuite/btcd/btcec/v2"

	"github.com/muun/libwallet/domain/action/recovery"
	"github.com/muun/libwallet/service"
	"github.com/muun/libwallet/service/model"
	"github.com/muun/libwallet/storage"
)

type FinishChallengeSetupAction struct {
	houstonService                  service.HoustonService
	keyValueStorage                 *storage.KeyValueStorage
	computeAndStoreEncryptedMeenKey *recovery.ComputeAndStoreEncryptedMeenKeyAction
}

func NewFinishChallengeSetupAction(
	houstonService service.HoustonService,
	keyValueStorage *storage.KeyValueStorage,
	computeAndStoreEncryptedMeenKey *recovery.ComputeAndStoreEncryptedMeenKeyAction,
) *FinishChallengeSetupAction {
	return &FinishChallengeSetupAction{
		houstonService,
		keyValueStorage,
		computeAndStoreEncryptedMeenKey,
	}
}

func (action *FinishChallengeSetupAction) Run(recoveryCodePublicKey *btcec.PublicKey) error {

	challengeSetupVerifyJson := model.ChallengeSetupVerifyJson{ //nolint:staticcheck // TODO: var challengeSetupVerifyJson should be challengeSetupVerifyJSON
		ChallengeType: "RECOVERY_CODE",
		PublicKey:     hex.EncodeToString(recoveryCodePublicKey.SerializeCompressed()),
	}

	verifiableMeenKeyJson, err := action.houstonService.ChallengeSetupFinishWithVerifiableMeenKey( //nolint:staticcheck // TODO: var verifiableMeenKeyJson should be verifiableMeenKeyJSON
		challengeSetupVerifyJson,
	)
	if err != nil {
		return err
	}

	// If an error occurs during verification we log it, but we do not return it.
	err = action.computeAndStoreEncryptedMeenKey.Run(
		recoveryCodePublicKey,
		&verifiableMeenKeyJson,
	)
	if err != nil {
		slog.Error(
			"An error occurred during encrypted meen key verification",
			slog.Any("error", err),
		)
	}
	return nil
}
