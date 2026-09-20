package recovery

import (
	"testing"

	"github.com/go-errors/errors"

	"github.com/muun/libwallet/internal/testutils"
	"github.com/muun/libwallet/storage"
)

func TestComputeAndStoreEncryptedMeenKeyAction(t *testing.T) {

	t.Run("routes to correct storage slot based on proof", func(t *testing.T) {
		testCases := []struct {
			desc           string
			withProof      bool
			expectedSlot   string
			unexpectedSlot string
		}{
			{
				"verified key when proof is provided",
				true,
				storage.VerifiedEncryptedMeenKey,
				storage.UnverifiedEncryptedMeenKey,
			},
			{
				"unverified key when no proof is provided",
				false,
				storage.UnverifiedEncryptedMeenKey,
				storage.VerifiedEncryptedMeenKey,
			},
		}
		for _, tc := range testCases {
			t.Run(tc.desc, func(t *testing.T) {
				// Setup
				keys := testutils.GenerateTestKeys()
				kvStorage := testutils.NewTestKeyValueStorage(t)
				keyProvider := testutils.NewMockKeyProvider(keys)
				action := NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
				vmkJSON := testutils.BuildVerifiableMeenKeyJson(
					keys,
					tc.withProof,
				)

				// Test
				err := action.Run(keys.RecoveryCodeKey.PubKey(), vmkJSON)
				if err != nil {
					t.Fatalf("Run() error = %v", err)
				}

				// Verify expected slot has data
				got, err := kvStorage.Get(tc.expectedSlot)
				if err != nil {
					t.Fatalf("Get(%s) error = %v", tc.expectedSlot, err)
				}
				if got == nil {
					t.Fatalf("expected key to be stored in %s", tc.expectedSlot)
				}

				// Verify unexpected slot is empty
				got, err = kvStorage.Get(tc.unexpectedSlot)
				if err != nil {
					t.Fatalf("Get(%s) error = %v", tc.unexpectedSlot, err)
				}
				if got != nil {
					t.Fatalf("expected %s to be empty", tc.unexpectedSlot)
				}
			})
		}
	})

	t.Run("overwrites existing key on second run", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		action := NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)

		// First run
		vmkJSON1 := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)
		err := action.Run(keys.RecoveryCodeKey.PubKey(), vmkJSON1)
		if err != nil {
			t.Fatalf("first Run() error = %v", err)
		}
		first, _ := kvStorage.Get(storage.VerifiedEncryptedMeenKey)

		// Second run (HPKE uses ephemeral keys, so ciphertext differs)
		vmkJSON2 := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)
		err = action.Run(keys.RecoveryCodeKey.PubKey(), vmkJSON2)
		if err != nil {
			t.Fatalf("second Run() error = %v", err)
		}
		second, _ := kvStorage.Get(storage.VerifiedEncryptedMeenKey)

		// Verify value changed
		if first.(string) == second.(string) {
			t.Fatal("expected second Run to overwrite the first key with a different value")
		}
	})

	t.Run("propagates user key error", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		keyProvider.UserPrivateKeyErr = errors.New("keystore locked")
		action := NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
		vmkJSON := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)

		// Test
		err := action.Run(keys.RecoveryCodeKey.PubKey(), vmkJSON)
		if err == nil {
			t.Fatal("expected error when UserPrivateKey fails")
		}
	})

	t.Run("propagates meen key error", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		keyProvider.MeenPublicKeyErr = errors.New("keystore locked")
		action := NewComputeAndStoreEncryptedMeenKeyAction(kvStorage, keyProvider)
		vmkJSON := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)

		// Test
		err := action.Run(keys.RecoveryCodeKey.PubKey(), vmkJSON)
		if err == nil {
			t.Fatal("expected error when MeenPublicKey fails")
		}
	})
}
