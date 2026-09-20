package recovery

import (
	"testing"

	"github.com/go-errors/errors"

	"github.com/meen/libwallet/internal/testutils"
	"github.com/meen/libwallet/storage"
)

func TestPopulateEncryptedMeenKeyAction(t *testing.T) {

	t.Run("stores key in correct slot when storage is empty", func(t *testing.T) {
		testCases := []struct {
			desc         string
			withProof    bool
			expectedSlot string
		}{
			{"verified key when proof is provided", true, storage.VerifiedEncryptedMeenKey},
			{"unverified key when no proof is provided", false, storage.UnverifiedEncryptedMeenKey},
		}
		for _, tc := range testCases {
			t.Run(tc.desc, func(t *testing.T) {
				// Setup
				keys := testutils.GenerateTestKeys()
				kvStorage := testutils.NewTestKeyValueStorage(t)
				keyProvider := testutils.NewMockKeyProvider(keys)
				vmkJSON := testutils.BuildVerifiableMeenKeyJson(
					keys,
					tc.withProof,
				)
				houston := &testutils.MockHoustonService{
					VerifiableMeenKeyResult: *vmkJSON,
				}
				action := NewPopulateEncryptedMeenKeyAction(houston, kvStorage, keyProvider)

				// Test
				err := action.Run(keys.RecoveryCodeKey.PubKey())
				if err != nil {
					t.Fatalf("Run() error = %v", err)
				}

				// Verify
				got, err := kvStorage.Get(tc.expectedSlot)
				if err != nil {
					t.Fatalf("Get(%s) error = %v", tc.expectedSlot, err)
				}
				if got == nil {
					t.Fatalf("expected key to be stored in %s", tc.expectedSlot)
				}
			})
		}
	})

	t.Run("returns early when verified key already exists", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		err := kvStorage.Save(storage.VerifiedEncryptedMeenKey, "existing-verified-key")
		if err != nil {
			t.Fatalf("Save() error = %v", err)
		}

		// Houston should NOT be called — if it is, the mock will return an error
		houston := &testutils.MockHoustonService{
			VerifiableMeenKeyErr: errors.New("should not be called"),
		}
		action := NewPopulateEncryptedMeenKeyAction(houston, kvStorage, keyProvider)

		// Test
		err = action.Run(keys.RecoveryCodeKey.PubKey())
		if err != nil {
			t.Fatalf("Run() error = %v, expected early return", err)
		}

		// Verify key is unchanged
		got, _ := kvStorage.Get(storage.VerifiedEncryptedMeenKey)
		if got.(string) != "existing-verified-key" {
			t.Fatal("verified key should not have been modified")
		}
	})

	t.Run("does not overwrite existing unverified key with another unverified", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		err := kvStorage.Save(storage.UnverifiedEncryptedMeenKey, "existing-unverified-key")
		if err != nil {
			t.Fatalf("Save() error = %v", err)
		}
		vmkJSON := testutils.BuildVerifiableMeenKeyJson(
			keys,
			false,
		)
		houston := &testutils.MockHoustonService{
			VerifiableMeenKeyResult: *vmkJSON,
		}
		action := NewPopulateEncryptedMeenKeyAction(houston, kvStorage, keyProvider)

		// Test
		err = action.Run(keys.RecoveryCodeKey.PubKey())
		if err != nil {
			t.Fatalf("Run() error = %v", err)
		}

		// Verify key is unchanged
		got, _ := kvStorage.Get(storage.UnverifiedEncryptedMeenKey)
		if got.(string) != "existing-unverified-key" {
			t.Fatal("unverified key should not have been overwritten")
		}
	})

	t.Run("upgrades unverified to verified when proof becomes available", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		err := kvStorage.Save(storage.UnverifiedEncryptedMeenKey, "existing-unverified-key")
		if err != nil {
			t.Fatalf("Save() error = %v", err)
		}
		vmkJSON := testutils.BuildVerifiableMeenKeyJson(
			keys,
			true,
		)
		houston := &testutils.MockHoustonService{
			VerifiableMeenKeyResult: *vmkJSON,
		}
		action := NewPopulateEncryptedMeenKeyAction(houston, kvStorage, keyProvider)

		// Test
		err = action.Run(keys.RecoveryCodeKey.PubKey())
		if err != nil {
			t.Fatalf("Run() error = %v", err)
		}

		// Verify verified key was stored
		got, _ := kvStorage.Get(storage.VerifiedEncryptedMeenKey)
		if got == nil {
			t.Fatal("expected verified key to be stored after upgrade")
		}

		// Verify unverified key was NOT deleted (production code writes verified but keeps
		// unverified)
		unverified, _ := kvStorage.Get(storage.UnverifiedEncryptedMeenKey)
		if unverified == nil || unverified.(string) != "existing-unverified-key" {
			t.Fatal("unverified key should remain in storage after upgrade")
		}
	})

	t.Run("propagates houston error", func(t *testing.T) {
		// Setup
		keys := testutils.GenerateTestKeys()
		kvStorage := testutils.NewTestKeyValueStorage(t)
		keyProvider := testutils.NewMockKeyProvider(keys)
		houston := &testutils.MockHoustonService{
			VerifiableMeenKeyErr: errors.New("houston error"),
		}
		action := NewPopulateEncryptedMeenKeyAction(houston, kvStorage, keyProvider)

		// Test
		err := action.Run(keys.RecoveryCodeKey.PubKey())
		if err == nil {
			t.Fatal("expected error from Houston to propagate")
		}
	})
}
