package io.meen.apollo.data.net.base;

import io.meen.apollo.domain.errors.ErrorClassification;
import io.meen.apollo.domain.errors.MeenError;

import org.jetbrains.annotations.NotNull;

public class ServerFailureException extends MeenError {

    @NotNull
    @Override
    public ErrorClassification getClassification() {
        return ErrorClassification.UNEXPECTED;
    }

    public ServerFailureException(Throwable cause) {
        super("We're facing a temporary issue. Please, try again later", cause);
    }
}
