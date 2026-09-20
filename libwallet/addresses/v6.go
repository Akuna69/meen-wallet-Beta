package addresses

import (
	"github.com/btcsuite/btcd/btcutil/hdkeychain"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet/btcsuitew/btcutilw"
	"github.com/meen/libwallet/musig"
)

// CreateAddressV6 returns a P2TR WalletAddress using Musig2v100 with the signing and cosigning
// keys.
func CreateAddressV6(
	userKey, meenKey *hdkeychain.ExtendedKey,
	path string,
	network *chaincfg.Params,
) (*WalletAddress, error) {
	witnessProgram, err := CreateWitnessScriptV6(userKey, meenKey)
	if err != nil {
		return nil, errors.Errorf("failed to generate witness script v5: %w", err)
	}

	address, err := btcutilw.NewAddressTaprootKey(witnessProgram, network)
	if err != nil {
		return nil, err
	}

	return &WalletAddress{
		address:        address.EncodeAddress(),
		version:        V6,
		derivationPath: path,
	}, nil
}

func CreateWitnessScriptV6(userKey, meenKey *hdkeychain.ExtendedKey) ([]byte, error) {
	userPublicKey, err := userKey.ECPubKey()
	if err != nil {
		return nil, errors.Errorf("error getting pub key: %w", err)
	}
	meenPublicKey, err := meenKey.ECPubKey()
	if err != nil {
		return nil, errors.Errorf("error getting pub key: %w", err)
	}

	pubKeys := [][]byte{
		userPublicKey.SerializeCompressed(),
		meenPublicKey.SerializeCompressed(),
	}

	tweak := musig.KeySpendOnlyTweak()

	aggregateKey, err := musig.Musig2CombinePubKeysWithTweak(musig.Musig2v100, pubKeys, tweak)
	if err != nil {
		return nil, errors.Errorf("error combining keys: %w", err)
	}

	xOnlyCombined := aggregateKey.FinalKey.SerializeCompressed()[1:]

	return xOnlyCombined, nil
}
