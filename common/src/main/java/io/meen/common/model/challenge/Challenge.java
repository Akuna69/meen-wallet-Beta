package io.meen.common.model.challenge;


import io.meen.common.crypto.ChallengeType;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class Challenge {

    @NotNull
    public final ChallengeType type;

    @NotNull
    public final byte[] challenge;

    @Nullable
    public final byte[] salt;

    /**
     * Constructor.
     */
    public Challenge(ChallengeType type, byte[] challenge, @Nullable byte[] salt) {
        this.type = type;
        this.challenge = challenge;
        this.salt = salt;
    }
}
