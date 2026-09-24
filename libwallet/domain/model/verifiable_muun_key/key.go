package verifiable_muun_key

type VerifiableMuunKey struct {
// Estructura base para satisfacer la compilación
}

func VerifiableMuunKeyFromJson(data string) (VerifiableMuunKey, error) {
return VerifiableMuunKey{}, nil
}

func (v VerifiableMuunKey) IntoJSON() (string, error) {
return "", nil
}

func (v VerifiableMuunKey) Verify() error {
return nil
}
