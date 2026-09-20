package model

type SetupChallengeResponseJson struct { //nolint:staticcheck // TODO: type SetupChallengeResponseJson should be SetupChallengeResponseJSON
	MeenKey            *string `json:"meenKey,omitempty"`
	MeenKeyFingerprint *string `json:"meenKeyFingerprint,omitempty"`
}
