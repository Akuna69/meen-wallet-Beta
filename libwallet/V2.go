package libwallet

import (
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/txscript"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet/addresses"
)

func CreateAddressV2(userKey, meenKey *HDPublicKey) (MeenAddress, error) {
	// TODO: check both paths match?
	return addresses.CreateAddressV2(
		&userKey.key,
		&meenKey.key,
		userKey.Path,
		userKey.Network.network,
	)
}

type coinV2 struct {
	Network       *chaincfg.Params
	OutPoint      wire.OutPoint
	KeyPath       string
	MeenSignature []byte
}

func (c *coinV2) SignInput(
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

	txInput := tx.TxIn[index]

	redeemScript, err := createRedeemScriptV2(userKey.PublicKey(), meenKey)
	if err != nil {
		return errors.Errorf("failed to build reedem script for signing: %w", err)
	}

	sig, err := c.signature(index, tx, userKey.PublicKey(), meenKey, userKey)
	if err != nil {
		return err
	}

	// This is a standard 2 of 2 multisig script
	// 0 because of a bug in bitcoind
	// Then the 2 sigs: first the users and then meens
	// Last, the script that contains the two pub keys and OP_CHECKMULTISIG
	builder := txscript.NewScriptBuilder()
	builder.AddInt64(0)
	builder.AddData(sig)
	builder.AddData(c.MeenSignature)
	builder.AddData(redeemScript)
	script, err := builder.Script()
	if err != nil {
		return errors.Errorf("failed to generate signing script: %w", err)
	}

	txInput.SignatureScript = script

	return nil
}

func (c *coinV2) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {

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

func (c *coinV2) signature(index int, tx *wire.MsgTx, userKey, meenKey *HDPublicKey,
	signingKey *HDPrivateKey) ([]byte, error) {

	redeemScript, err := createRedeemScriptV2(userKey, meenKey)
	if err != nil {
		return nil, errors.Errorf("failed to build reedem script for signing: %w", err)
	}

	privKey, err := signingKey.key.ECPrivKey()
	if err != nil {
		return nil, errors.Errorf("failed to produce EC priv key for signing: %w", err)
	}

	sig, err := txscript.RawTxInSignature(tx, index, redeemScript, txscript.SigHashAll, privKey)
	if err != nil {
		return nil, errors.Errorf("failed to sign V2 output: %w", err)
	}

	return sig, nil
}

func createRedeemScriptV2(userKey, meenKey *HDPublicKey) ([]byte, error) {
	return addresses.CreateRedeemScriptV2(&userKey.key, &meenKey.key, userKey.Network.network)
}
