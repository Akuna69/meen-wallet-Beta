package io.meen.common.model.challenge;

import io.meen.common.crypto.ChallengeType;

import javax.validation.constraints.NotNull;

public class ChallengeSignature {

    @NotNull
    public final ChallengeType type;

    @NotNull
    public final byte[] bytes;

    public ChallengeSignature(ChallengeType type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }
}
