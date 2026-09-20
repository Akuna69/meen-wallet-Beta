package libwallet

import (
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet/addresses"
)

// CreateAddressV4 returns a P2WSH MeenAddress from a user HD-pubkey and a Meen co-signing
// HD-pubkey.
func CreateAddressV4(userKey, meenKey *HDPublicKey) (MeenAddress, error) {
	return addresses.CreateAddressV4(
		&userKey.key,
		&meenKey.key,
		userKey.Path,
		userKey.Network.network,
	)
}

type coinV4 struct {
	Network       *chaincfg.Params
	OutPoint      wire.OutPoint
	KeyPath       string
	Amount        btcutil.Amount
	MeenSignature []byte
}

func (c *coinV4) SignInput(
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
		return errors.Errorf("meen signature must be present: %w", err)
	}

	witnessScript, err := createWitnessScriptV4(userKey.PublicKey(), meenKey)
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

func (c *coinV4) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {

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

func (c *coinV4) signature(index int, tx *wire.MsgTx, userKey *HDPublicKey, meenKey *HDPublicKey,
	signingKey *HDPrivateKey) ([]byte, error) {

	witnessScript, err := createWitnessScriptV4(userKey, meenKey)
	if err != nil {
		return nil, err
	}

	return signNativeSegwitInputV0(
		index, tx, signingKey, witnessScript, c.Amount)
}

func createWitnessScriptV4(userKey, meenKey *HDPublicKey) ([]byte, error) {
	return addresses.CreateWitnessScriptV4(&userKey.key, &meenKey.key, userKey.Network.network)
}
