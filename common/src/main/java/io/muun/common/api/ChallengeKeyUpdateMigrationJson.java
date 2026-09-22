package io.muun.common.api;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class ChallengeKeyUpdateMigrationJson {

    @NotNull
    public String passwordKeySaltInHex;

    @Nullable
    public String recoveryCodeKeySaltInHex;

    @Nullable
    public String newEncrytpedMeenKey;

    /**
     * Json constructor.
     */
    public ChallengeKeyUpdateMigrationJson() {
    }

    /**
     * Data constructor.
     */
    public ChallengeKeyUpdateMigrationJson(String passwordKeySaltInHex,
                                           @Nullable String recoveryCodeKeySaltInHex,
                                           @Nullable String newEncrytpedMeenKey) {
        this.passwordKeySaltInHex = passwordKeySaltInHex;
        this.recoveryCodeKeySaltInHex = recoveryCodeKeySaltInHex;
        this.newEncrytpedMeenKey = newEncrytpedMeenKey;
    }
}
