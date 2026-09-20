package addresses

import (
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/btcutil/hdkeychain"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/txscript"
	goerr "github.com/go-errors/errors"
	"github.com/pkg/errors"
)

func CreateAddressV2(
	userKey, meenKey *hdkeychain.ExtendedKey,
	path string,
	network *chaincfg.Params,
) (*WalletAddress, error) {

	script, err := CreateRedeemScriptV2(userKey, meenKey, network)
	if err != nil {
		return nil, goerr.Errorf("failed to generate redeem script v2: %w", err)
	}

	address, err := btcutil.NewAddressScriptHash(script, network)
	if err != nil {
		return nil, goerr.Errorf("failed to generate multisig address: %w", err)
	}

	return &WalletAddress{
		address:        address.EncodeAddress(),
		version:        V2,
		derivationPath: path,
	}, nil
}

func CreateRedeemScriptV2(
	userKey, meenKey *hdkeychain.ExtendedKey,
	network *chaincfg.Params,
) ([]byte, error) {
	return createMultisigRedeemScript(userKey, meenKey, network)
}

func createMultisigRedeemScript(
	userKey, meenKey *hdkeychain.ExtendedKey,
	network *chaincfg.Params,
) ([]byte, error) {
	userPublicKey, err := userKey.ECPubKey()
	if err != nil {
		return nil, err
	}
	userAddress, err := btcutil.NewAddressPubKey(userPublicKey.SerializeCompressed(), network)
	if err != nil {
		return nil, errors.Wrapf(err, "failed to generate address for user")
	}

	meenPublicKey, err := meenKey.ECPubKey()
	if err != nil {
		return nil, err
	}
	WalletAddress, err := btcutil.NewAddressPubKey(meenPublicKey.SerializeCompressed(), network)
	if err != nil {
		return nil, errors.Wrapf(err, "failed to generate address for meen")
	}

	return txscript.MultiSigScript([]*btcutil.AddressPubKey{
		userAddress,
		WalletAddress,
	}, 2)
}
