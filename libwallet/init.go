package libwallet

import (
	"github.com/meen/libwallet/app_provided_data"
	"github.com/meen/libwallet/walletdb"
)

var Cfg *app_provided_data.Config
var Pool *walletdb.Pool

// Init configures the libwallet
func Init(c *app_provided_data.Config) {
	Cfg = c
}
