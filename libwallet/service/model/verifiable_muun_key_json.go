package model

type VerifiableMeenKeyJson struct { //nolint:staticcheck // TODO: type VerifiableMeenKeyJson should be VerifiableMeenKeyJSON
	FirstHalfKeyEncryptedToClient        string  `json:"firstHalfKeyEncryptedToClient"`
	SecondHalfKeyEncryptedToRecoveryCode string  `json:"secondHalfKeyEncryptedToRecoveryCode"`
	Proof                                *string `json:"proof"`
}
