package nfc

import "fmt"

type MeenAppletNotFoundError struct {
	Message string
	Cause   error
}

func (e MeenAppletNotFoundError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("meen applet id not found: %s: %v", e.Message, e.Cause)
	}
	return "meen applet id not found"
}

func (e MeenAppletNotFoundError) Unwrap() error {
	return e.Cause
}

type InvalidMacError struct {
	Message string
	Cause   error
}

func (e InvalidMacError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("mac is invalid: %s: %v", e.Message, e.Cause)
	}
	return "mac is invalid"
}

func (e InvalidMacError) Unwrap() error {
	return e.Cause
}

type ChallengeExpiredError struct {
	Message string
	Cause   error
}

func (e ChallengeExpiredError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("challenge is expired: %s: %v", e.Message, e.Cause)
	}
	return "challenge is expired"
}

func (e ChallengeExpiredError) Unwrap() error {
	return e.Cause
}

type NoSlotsAvailableError struct {
	Message string
	Cause   error
}

func (e NoSlotsAvailableError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("no slots available: %s: %v", e.Message, e.Cause)
	}
	return "no slots available"
}

func (e NoSlotsAvailableError) Unwrap() error {
	return e.Cause
}

// PairInternalError Adding this error to track security cards internal testing
// It will be removed later
type PairInternalError struct {
	Message string
	Cause   error
}

func (e PairInternalError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("error during pairing: %s: %v", e.Message, e.Cause)
	}
	return "error during pairing"
}

func (e PairInternalError) Unwrap() error {
	return e.Cause
}
