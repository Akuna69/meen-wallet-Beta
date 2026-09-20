package libwallet

import (
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet/addresses"
)

func CreateAddressV3(userKey, meenKey *HDPublicKey) (MeenAddress, error) {
	return addresses.CreateAddressV3(
		&userKey.key,
		&meenKey.key,
		userKey.Path,
		userKey.Network.network,
	)
}

type coinV3 struct {
	Network       *chaincfg.Params
	OutPoint      wire.OutPoint
	KeyPath       string
	Amount        btcutil.Amount
	MeenSignature []byte
}

func (c *coinV3) SignInput(
	index int,
	tx *wire.MsgTx,
	userKey *HDPrivateKey,
	meenKey *HDPublicKey,
) error {

	userKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive user key: %w", err)
	}

	meenKey, err = meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive meen key: %w", err)
	}

	if len(c.MeenSignature) == 0 {
		return errors.New("meen signature must be present")
	}

	witnessScript, err := createWitnessScriptV3(userKey.PublicKey(), meenKey)
	if err != nil {
		return err
	}

	sig, err := c.signature(index, tx, userKey.PublicKey(), meenKey, userKey)
	if err != nil {
		return err
	}

	zeroByteArray := []byte{}

	txInput := tx.TxIn[index]
	txInput.Witness = wire.TxWitness{zeroByteArray, sig, c.MeenSignature, witnessScript}

	return nil
}

func (c *coinV3) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {

	derivedUserKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive user key: %w", err)
	}

	derivedMeenKey, err := meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive meen key: %w", err)
	}

	meenSignature, err := c.signature(
		index,
		tx,
		derivedUserKey.PublicKey(),
		derivedMeenKey.PublicKey(),
		derivedMeenKey,
	)
	if err != nil {
		return err
	}
	c.MeenSignature = meenSignature
	return c.SignInput(index, tx, userKey, meenKey.PublicKey())
}

func createRedeemScriptV3(userKey, meenKey *HDPublicKey) ([]byte, error) {
	return addresses.CreateRedeemScriptV3(&userKey.key, &meenKey.key, userKey.Network.network)
}

func createWitnessScriptV3(userKey, meenKey *HDPublicKey) ([]byte, error) {
	return addresses.CreateWitnessScriptV3(&userKey.key, &meenKey.key, userKey.Network.network)
}

func (c *coinV3) signature(index int, tx *wire.MsgTx, userKey *HDPublicKey, meenKey *HDPublicKey,
	signingKey *HDPrivateKey) ([]byte, error) {

	witnessScript, err := createWitnessScriptV3(userKey, meenKey)
	if err != nil {
		return nil, err
	}

	redeemScript, err := createRedeemScriptV3(userKey, meenKey)
	if err != nil {
		return nil, errors.Errorf("failed to build reedem script for signing: %w", err)
	}

	return signNonNativeSegwitInputV0(
		index, tx, signingKey, redeemScript, witnessScript, c.Amount)
}
