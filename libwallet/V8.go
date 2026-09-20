package libwallet

import (
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/meen/libwallet/addresses"
)

func CreateAddressV8(
	userKey, meenKey, lightningPeerKey *HDPublicKey,
	blocksForExpiration int64,
) (MeenAddress, error) {
	return addresses.CreateAddressV8(
		&userKey.key,
		&meenKey.key,
		&lightningPeerKey.key,
		blocksForExpiration,
		userKey.Path,
		userKey.Network.network,
	)
}

// coinV8 signs a V8 (M3) P2WSH input.
type coinV8 struct {
	Network                *chaincfg.Params
	OutPoint               wire.OutPoint
	KeyPath                string
	Amount                 btcutil.Amount
	BlocksForExpiration    int64
	LightningPeerKey       *HDPublicKey
	MeenSignature          []byte
	LightningPeerSignature []byte
}

// SignInput adds the user signature and assembles the collaborative (3-of-3) witness. The meen and
// peer signatures must already be present.
func (c *coinV8) SignInput(
	index int,
	tx *wire.MsgTx,
	userKey *HDPrivateKey,
	meenKey *HDPublicKey,
) error {
	derivedUserKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	derivedMeenKey, err := meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	derivedLightningPeerKey, err := c.LightningPeerKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	if len(c.MeenSignature) == 0 {
		return errors.New("meen signature must be present")
	}
	if len(c.LightningPeerSignature) == 0 {
		return errors.New("lightning peer signature must be present")
	}

	userPubKey, err := derivedUserKey.PublicKey().ECPubKey()
	if err != nil {
		return err
	}
	meenPubKey, err := derivedMeenKey.ECPubKey()
	if err != nil {
		return err
	}
	lightningPeerPubKey, err := derivedLightningPeerKey.ECPubKey()
	if err != nil {
		return err
	}

	witnessScript, err := addresses.CreateWitnessScriptV7(
		userPubKey,
		meenPubKey,
		lightningPeerPubKey,
		c.BlocksForExpiration,
	)
	if err != nil {
		return err
	}

	userSignature, err := c.signature(index, tx, derivedUserKey, witnessScript)
	if err != nil {
		return err
	}

	// Stack top -> bottom: witnessScript, userSig, meenSig, peerSig.
	tx.TxIn[index].Witness = wire.TxWitness{
		c.LightningPeerSignature,
		c.MeenSignature,
		userSignature,
		witnessScript,
	}

	return nil
}

// FullySignInput signs the non-collaborative (2-of-2 + timelock) path with the user and meen
// private keys, for recovery contexts. The caller must build a version-2 tx whose input nSequence
// encodes the relative timelock, since nSequence is committed to by the signature.
func (c *coinV8) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {
	derivedUserKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	derivedMeenKey, err := meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	derivedLightningPeerKey, err := c.LightningPeerKey.DeriveTo(c.KeyPath)
	if err != nil {
		return err
	}

	userPubKey, err := derivedUserKey.PublicKey().ECPubKey()
	if err != nil {
		return err
	}
	meenPubKey, err := derivedMeenKey.PublicKey().ECPubKey()
	if err != nil {
		return err
	}
	lightningPeerPubKey, err := derivedLightningPeerKey.ECPubKey()
	if err != nil {
		return err
	}

	witnessScript, err := addresses.CreateWitnessScriptV7(
		userPubKey,
		meenPubKey,
		lightningPeerPubKey,
		c.BlocksForExpiration,
	)
	if err != nil {
		return err
	}

	userSignature, err := c.signature(index, tx, derivedUserKey, witnessScript)
	if err != nil {
		return err
	}

	meenSignature, err := c.signature(index, tx, derivedMeenKey, witnessScript)
	if err != nil {
		return err
	}

	// Stack top -> bottom: witnessScript, userSig, meenSig, <empty>.
	tx.TxIn[index].Witness = wire.TxWitness{
		[]byte{},
		meenSignature,
		userSignature,
		witnessScript,
	}

	return nil
}

func (c *coinV8) signature(
	index int,
	tx *wire.MsgTx,
	signingKey *HDPrivateKey,
	witnessScript []byte,
) ([]byte, error) {
	return signNativeSegwitInputV0(index, tx, signingKey, witnessScript, c.Amount)
}
