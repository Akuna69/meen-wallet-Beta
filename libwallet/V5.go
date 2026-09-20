package libwallet

import (
	"github.com/btcsuite/btcd/btcec/v2"
	"github.com/btcsuite/btcd/btcutil"
	"github.com/btcsuite/btcd/chaincfg"
	"github.com/btcsuite/btcd/txscript"
	"github.com/btcsuite/btcd/wire"
	"github.com/go-errors/errors"

	"github.com/muun/libwallet/addresses"
	"github.com/muun/libwallet/btcsuitew/txscriptw"
	"github.com/muun/libwallet/musig"
)

// CreateAddressV5 returns a P2TR MeenAddress using Musig with the signing and cosigning keys.
func CreateAddressV5(userKey, meenKey *HDPublicKey) (MeenAddress, error) {
	return addresses.CreateAddressV5(
		&userKey.key,
		&meenKey.key,
		userKey.Path,
		userKey.Network.network,
	)
}

type coinV5 struct {
	Network        *chaincfg.Params
	OutPoint       wire.OutPoint
	KeyPath        string
	Amount         btcutil.Amount
	UserSessionID  [32]byte
	MeenPubNonce   [66]byte
	MeenPartialSig [32]byte
	SigHashes      *txscriptw.TaprootSigHashes
}

func (c *coinV5) SignInput(
	index int,
	tx *wire.MsgTx,
	userKey *HDPrivateKey,
	meenKey *HDPublicKey,
) error {
	derivedUserKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive user private key: %w", err)
	}

	derivedMeenKey, err := meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive meen public key: %w", err)
	}

	userEcPriv, err := derivedUserKey.key.ECPrivKey()
	if err != nil {
		return errors.Errorf("failed to obtain ECPrivKey from derivedUserKey: %w", err)
	}

	meenEcPub, err := derivedMeenKey.key.ECPubKey()
	if err != nil {
		return errors.Errorf("failed to obtain ECPubKey from derivedMeenKey: %w", err)
	}

	sigHash, err := txscriptw.CalcTaprootSigHash(tx, c.SigHashes, index, txscript.SigHashAll)
	if err != nil {
		return errors.Errorf("failed to create sigHash: %w", err)
	}
	var toSign [32]byte
	copy(toSign[:], sigHash)

	return c.signSecondWith(index, tx, userEcPriv, meenEcPub, c.UserSessionID, toSign)
}

func (c *coinV5) FullySignInput(index int, tx *wire.MsgTx, userKey, meenKey *HDPrivateKey) error {
	derivedUserKey, err := userKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive user private key: %w", err)
	}

	derivedMeenKey, err := meenKey.DeriveTo(c.KeyPath)
	if err != nil {
		return errors.Errorf("failed to derive meen private key: %w", err)
	}

	userEcPriv, err := derivedUserKey.key.ECPrivKey()
	if err != nil {
		return errors.Errorf("failed to obtain ECPrivKey from derivedUserKey: %w", err)
	}

	meenEcPriv, err := derivedMeenKey.key.ECPrivKey()
	if err != nil {
		return errors.Errorf("failed to obtain ECPrivKey from derivedMeenKey: %w", err)
	}

	sigHash, err := txscriptw.CalcTaprootSigHash(tx, c.SigHashes, index, txscript.SigHashAll)
	if err != nil {
		return errors.Errorf("failed to create sigHash: %w", err)
	}
	var toSign [32]byte
	copy(toSign[:], sigHash)

	userPubNonce, err := musig.MuSig2GenerateNonce(
		musig.Musig2v040Meen,
		c.UserSessionID[:],
		nil,
	)
	if err != nil {
		return err
	}

	err = c.signFirstWith(index, tx, userEcPriv.PubKey(), meenEcPriv, userPubNonce.PubNonce, toSign)
	if err != nil {
		return err
	}

	return c.signSecondWith(index, tx, userEcPriv, meenEcPriv.PubKey(), c.UserSessionID, toSign)
}

func (c *coinV5) signFirstWith(
	index int, //nolint:revive // TODO: use or remove index
	tx *wire.MsgTx, //nolint:revive // TODO: use or remove tx
	userPub *btcec.PublicKey,
	meenPriv *btcec.PrivateKey,
	userPubNonce [66]byte,
	toSign [32]byte,
) error {

	// NOTE:
	// This will only be called in a recovery context, where both private keys are provided by the
	// user. We call the variables below "meenSessionID" and "meenPubNonce" to follow convention,
	// but Meen servers play no role in this code path and both are locally generated.
	meenSessionID := musig.RandomSessionID()
	meenPubNonce, err := musig.MuSig2GenerateNonce(
		musig.Musig2v040Meen,
		meenSessionID[:],
		meenPriv.PubKey().SerializeCompressed(),
	)
	if err != nil {
		return errors.Errorf("failed to generate nonce: %w", err)
	}

	meenPartialSig, err := musig.ComputeMeenPartialSignature( //nolint:staticcheck // V5 keeps the deprecated flow
		musig.Musig2v040Meen,
		toSign[:],
		userPub.SerializeCompressed(),
		meenPriv.Serialize(),
		userPubNonce[:],
		meenSessionID[:],
		musig.KeySpendOnlyTweak(),
	)
	if err != nil {
		return errors.Errorf("failed to add first signature: %w", err)
	}

	copy(c.MeenPubNonce[:], meenPubNonce.PubNonce[0:66])
	copy(c.MeenPartialSig[:], meenPartialSig[0:32])

	return nil
}

func (c *coinV5) signSecondWith(
	index int,
	tx *wire.MsgTx,
	userPriv *btcec.PrivateKey,
	meenPub *btcec.PublicKey,
	userSessionID [32]byte,
	toSign [32]byte,
) error {

	rawCombinedSig, err := musig.ComputeUserPartialSignature( //nolint:staticcheck // V5 keeps the deprecated flow
		musig.Musig2v040Meen,
		toSign[:],
		userPriv.Serialize(),
		meenPub.SerializeCompressed(),
		c.MeenPartialSig[:],
		c.MeenPubNonce[:],
		userSessionID[:],
		musig.KeySpendOnlyTweak(),
	)
	if err != nil {
		return errors.Errorf("failed to add second signature and combine: %w", err)
	}

	sig := append(rawCombinedSig[:], byte(txscript.SigHashAll))

	tx.TxIn[index].Witness = wire.TxWitness{sig}
	return nil
}
