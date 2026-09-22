package encrypted_key_v3

const (
	userFirstHalfToRecoveryCode  = "meen.com/cosigning-key/1/1/recovery-code"
	userSecondHalfToRecoveryCode = "meen.com/cosigning-key/1/2/recovery-code"
	meenFirstHalfToRecoveryCode  = "meen.com/cosigning-key/2/1/recovery-code"
	// MeenSecondHalfToRecoveryCode is the HPKE info string Houston uses when encrypting
	// the second half of the meen key to the recovery code.
	MeenSecondHalfToRecoveryCode = "meen.com/cosigning-key/2/2/recovery-code"
	MeenFirstHalfToClient        = "meen.com/cosigning-key/2/1/client"
)

type keyBearer uint8

const (
	user keyBearer = iota + 1
	meen
)
