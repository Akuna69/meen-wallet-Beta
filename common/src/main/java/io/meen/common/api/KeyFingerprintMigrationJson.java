package io.meen.common.api;

import javax.validation.constraints.NotNull;

public class KeyFingerprintMigrationJson {

    @NotNull
    public String meenKeyFingerprint;

    /**
     * Json constructor.
     */
    public KeyFingerprintMigrationJson() {
    }

    /**
     * Data constructor.
     */
    public KeyFingerprintMigrationJson(@NotNull String meenKeyFingerprint) {
        this.meenKeyFingerprint = meenKeyFingerprint;
    }
}
