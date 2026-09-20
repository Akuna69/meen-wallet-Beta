package challenge_keys

import (
	"encoding/hex"
	"testing"

	"github.com/go-errors/errors"

	"github.com/meen/libwallet/domain/action/recovery"
	"github.com/meen/libwallet/internal/testutils"
	"github.com/meen/libwallet/storage"
)

func TestFinishChallengeSetupAction(t *testing.T) {

	t.Run("calls houston and stores encrypted meen key", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		vmkJSON := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)
		houston := &testutils.MockHoustonService{
			FinishWithVerifiableResult: *vmkJSON,
		}
		computeAction := recovery.NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
		action := NewFinishChallengeSetupAction(houston, kvStorage, computeAction)

		// Test
		err := action.Run(keys.RecoveryCodeKey.PubKey())
		if err != nil {
			t.Fatalf("Run() error = %v", err)
		}

		// Verify houston was called with correct challenge type and public key
		if houston.CapturedChallengeSetupVerify == nil {
			t.Fatal("expected Houston to be called")
		}

		wantType := "RECOVERY_CODE"
		if houston.CapturedChallengeSetupVerify.ChallengeType != wantType {
			t.Fatalf("ChallengeType = %s, want %s",
				houston.CapturedChallengeSetupVerify.ChallengeType, wantType)
		}

		wantPubKey := hex.EncodeToString(keys.RecoveryCodeKey.PubKey().SerializeCompressed())
		if houston.CapturedChallengeSetupVerify.PublicKey != wantPubKey {
			t.Fatalf("PublicKey = %s, want %s",
				houston.CapturedChallengeSetupVerify.PublicKey, wantPubKey)
		}

		// Verify encrypted meen key was stored
		got, err := kvStorage.Get(storage.VerifiedEncryptedMeenKey)
		if err != nil {
			t.Fatalf("Get() error = %v", err)
		}
		if got == nil {
			t.Fatal("expected verified encrypted meen key to be stored")
		}
	})

	t.Run("swallows verification error", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		houston := &testutils.MockHoustonService{
			FinishWithVerifiableResult: testutils.BuildInvalidVerifiableMeenKeyJson(),
		}
		computeAction := recovery.NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
		action := NewFinishChallengeSetupAction(houston, kvStorage, computeAction)

		// Test — should NOT return error even though verification fails
		err := action.Run(keys.RecoveryCodeKey.PubKey())
		if err != nil {
			t.Fatalf("Run() error = %v, expected error to be swallowed", err)
		}
	})

	t.Run("propagates houston error", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		houston := &testutils.MockHoustonService{
			FinishWithVerifiableErr: errors.New("houston network error"),
		}
		computeAction := recovery.NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
		action := NewFinishChallengeSetupAction(houston, kvStorage, computeAction)

		// Test
		err := action.Run(keys.RecoveryCodeKey.PubKey())
		if err == nil {
			t.Fatal("expected Houston error to propagate")
		}
	})
}
