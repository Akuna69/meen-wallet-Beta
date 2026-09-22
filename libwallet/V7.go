package libwallet

import (
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/muun/libwallet/addresses"
)

func CreateAddressV7(
	userKey, meenKey, lightningPeerKey *HDPublicKey,
	blocksForExpiration int64,
) (MeenAddress, error) {
	return addresses.CreateAddressV7(
		&userKey.key,
		&meenKey.key,
		&lightningPeerKey.key,
		blocksForExpiration,
		userKey.Path,
		userKey.Network.network,
	)
}

// coinV7 signs a V7 (M3) P2SH-P2WSH input.
type coinV7 struct {
	Network             *chaincfg.Params
	OutPoint            wire.OutPoint
	KeyPath             string
	Amount              btcutil.Amount
	BlocksForExpiration int64
	LightningPeerKey    *HDPublicKey
	MeenSignature       []byte
	PeerSignature       []byte
}

// SignInput adds the user signature and assembles the collaborative (3-of-3) witness. The meen and
// peer signatures must already be present.
func (c *coinV7) SignInput(
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
	if len(c.PeerSignature) == 0 {
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

	userSignature, err := c.signature(
		index,
		tx,
		derivedUserKey,
		witnessScript,
	)
	if err != nil {
		return err
	}

	// Stack top -> bottom: witnessScript, userSig, meenSig, peerSig.
	tx.TxIn[index].Witness = wire.TxWitness{
		c.PeerSignature,
		c.MeenSignature,
		userSignature,
		witnessScript,
	}

	return nil
}

// FullySignInput signs the non-collaborative (2-of-2 + timelock) path with the user and meen
// private keys, for recovery contexts. The caller must build a version-2 tx whose input nSequence
// encodes the relative timelock, since nSequence is committed to by the signature.
func (c *coinV7) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {
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

	userSignature, err := c.signature(
		index,
		tx,
		derivedUserKey,
		witnessScript,
	)
	if err != nil {
		return err
	}

	meenSignature, err := c.signature(
		index,
		tx,
		derivedMeenKey,
		witnessScript,
	)
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

func (c *coinV7) signature(
	index int,
	tx *wire.MsgTx,
	signingKey *HDPrivateKey,
	witnessScript []byte,
) ([]byte, error) {

	redeemScript, err := addresses.CreateRedeemScriptV7(witnessScript)
	if err != nil {
		return nil, err
	}

	return signNonNativeSegwitInputV0(
		index, tx, signingKey, redeemScript, witnessScript, c.Amount)
}
