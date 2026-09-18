package io.meen.common.crypto.hd.exception;

public class InvalidDerivationPathException extends RuntimeException {

    public InvalidDerivationPathException(String path) {
        super("Invalid derivation path: " + path);
    }
}
