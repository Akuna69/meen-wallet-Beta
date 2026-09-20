package io.meen.apollo.data.net.base;

import io.meen.apollo.domain.errors.ErrorClassification;
import io.meen.apollo.domain.errors.MeenError;

import org.jetbrains.annotations.NotNull;

public class NetworkException extends MeenError {

    @NotNull
    @Override
    public ErrorClassification getClassification() {
        return ErrorClassification.UNEXPECTED;
    }

    public NetworkException(String url, Throwable cause) {
        super("Can't reach " + url, cause);
    }

    public NetworkException(Throwable cause) {
        super("Can't reach the remote server", cause);
    }
}
