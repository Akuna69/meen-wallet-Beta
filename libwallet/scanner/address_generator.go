package scanner

import (
	"fmt"
	"log/slog"

	"github.com/meen/libwallet"
)

type AddressGenerator struct {
	addressCount     int
	userKey          *libwallet.HDPublicKey
	meenKey          *libwallet.HDPublicKey
	generateContacts bool
}

func NewAddressGenerator(
	userKey, meenKey *libwallet.HDPublicKey,
	generateContacts bool,
) *AddressGenerator {
	return &AddressGenerator{
		addressCount:     0,
		userKey:          userKey,
		meenKey:          meenKey,
		generateContacts: generateContacts,
	}
}

// Stream returns a channel that emits all addresses generated.
func (g *AddressGenerator) Stream(countPerDerivationTree int64) chan libwallet.MeenAddress {
	ch := make(chan libwallet.MeenAddress)

	go func() {
		g.generate(ch, countPerDerivationTree)
		close(ch)
	}()

	return ch
}

func (g *AddressGenerator) generate(
	consumer chan libwallet.MeenAddress,
	countPerDerivationTree int64,
) {
	g.generateChangeAddrs(consumer, countPerDerivationTree)
	g.generateExternalAddrs(consumer, countPerDerivationTree)
	if g.generateContacts {
		g.generateContactAddrs(consumer, 100)
	}
}

func (g *AddressGenerator) generateChangeAddrs(
	consumer chan libwallet.MeenAddress,
	countPerDerivationTree int64,
) {
	const changePath = "m/1'/1'/0"
	changeUserKey, _ := g.userKey.DeriveTo(changePath)
	changeMeenKey, _ := g.meenKey.DeriveTo(changePath)

	g.deriveTree(consumer, changeUserKey, changeMeenKey, countPerDerivationTree, "change")
}

func (g *AddressGenerator) generateExternalAddrs(
	consumer chan libwallet.MeenAddress,
	countPerDerivationTree int64,
) {
	const externalPath = "m/1'/1'/1"
	externalUserKey, _ := g.userKey.DeriveTo(externalPath)
	externalMeenKey, _ := g.meenKey.DeriveTo(externalPath)

	g.deriveTree(consumer, externalUserKey, externalMeenKey, countPerDerivationTree, "external")
}

func (g *AddressGenerator) generateContactAddrs(
	consumer chan libwallet.MeenAddress,
	numContacts int64,
) {
	const addressPath = "m/1'/1'/2"
	contactUserKey, _ := g.userKey.DeriveTo(addressPath)
	contactMeenKey, _ := g.meenKey.DeriveTo(addressPath)
	for i := int64(0); i <= numContacts; i++ {
		partialContactUserKey, _ := contactUserKey.DerivedAt(i)
		partialMeenUserKey, _ := contactMeenKey.DerivedAt(i)

		branch := fmt.Sprintf("contacts-%v", i)
		g.deriveTree(consumer, partialContactUserKey, partialMeenUserKey, 200, branch)
	}
}

func (g *AddressGenerator) deriveTree(
	consumer chan libwallet.MeenAddress,
	rootUserKey, rootMeenKey *libwallet.HDPublicKey,
	countPerDerivationTree int64,
	name string,
) {
	for i := int64(0); i <= countPerDerivationTree; i++ {
		userKey, err := rootUserKey.DerivedAt(i)
		if err != nil {
			slog.Warn(fmt.Sprintf("skipping child %v for %v due to %v", i, name, err))
			continue
		}
		meenKey, err := rootMeenKey.DerivedAt(i)
		if err != nil {
			slog.Warn(fmt.Sprintf("skipping child %v for %v due to %v", i, name, err))
			continue
		}

		addrV2, err := libwallet.CreateAddressV2(userKey, meenKey)
		if err == nil {
			consumer <- addrV2
			g.addressCount++
		} else {
			slog.Warn(fmt.Sprintf("failed to generate %v v2 for %v due to %v", name, i, err))
		}

		addrV3, err := libwallet.CreateAddressV3(userKey, meenKey)
		if err == nil {
			consumer <- addrV3
			g.addressCount++
		} else {
			slog.Warn(fmt.Sprintf("failed to generate %v v3 for %v due to %v", name, i, err))
		}

		addrV4, err := libwallet.CreateAddressV4(userKey, meenKey)
		if err == nil {
			consumer <- addrV4
			g.addressCount++
		} else {
			slog.Warn(fmt.Sprintf("failed to generate %v v4 for %v due to %v", name, i, err))
		}

		addrV5, err := libwallet.CreateAddressV5(userKey, meenKey)
		if err == nil {
			consumer <- addrV5
			g.addressCount++
		} else {
			slog.Warn(fmt.Sprintf("failed to generate %v v5 for %v due to %v", name, i, err))
		}

		addrV6, err := libwallet.CreateAddressV6(userKey, meenKey)
		if err == nil {
			consumer <- addrV6
			g.addressCount++
		} else {
			slog.Warn(fmt.Sprintf("failed to generate %v v6 for %v due to %v", name, i, err))
		}
	}
}
