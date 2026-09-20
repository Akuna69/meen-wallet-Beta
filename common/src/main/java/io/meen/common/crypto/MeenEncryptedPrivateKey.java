package io.meen.common.crypto;

import io.meen.common.utils.Preconditions;
import io.meen.common.utils.internal.Base58;

public interface MeenEncryptedPrivateKey {
    int PUBLIC_KEY_SIZE = 33;

    enum Version {
        V2,
        V3;

        static Version fromEncryptedPrivateKey(String serializedPrivateKey) {
            final byte[] decodedBytes = Base58.decode(serializedPrivateKey);
            final int firstByte = decodedBytes[0];

            Preconditions.checkArgument(firstByte == 3 || firstByte == 2);

            if (firstByte == 3) {
                return V3;
            }

            return V2;
        }
    }

    String toBase58();

    byte[] getRecoveryCodeSalt();

    byte[] getEphemeralPublicKey();

    byte[] getCypherText();

    byte getVersion();

    /**
     * Factory MeenEncryptedPrivateKey abstracting the caller from the encryptedKey version.
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault") // We want to have a compiling error here
    // if a new value is added.
    static MeenEncryptedPrivateKey create(
            Version version,
            long birthday,
            byte[] ephemeralPublicKey,
            byte[] cypherText,
            byte[] recoveryCodeSalt
    ) {
        switch (version) {
            case V2:
                return new MeenEncryptedPrivateKeyV2(
                        birthday,
                        ephemeralPublicKey,
                        cypherText,
                        recoveryCodeSalt
                );
            case V3:
                return new MeenEncryptedPrivateKeyV3(
                        ephemeralPublicKey,
                        cypherText,
                        recoveryCodeSalt
                );
        }

        throw new IllegalStateException();
    }

    /**
     * Factory MeenEncryptedPrivateKey abstracting the caller from the encryptedKey version.
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault") // We want to have a compiling error here
    // if a new value is added.
    static MeenEncryptedPrivateKey fromBase58(
            String serialization
    ) {
        final Version version = Version.fromEncryptedPrivateKey(serialization);
        switch (version) {
            case V2:
                return MeenEncryptedPrivateKeyV2.fromBase58(serialization);
            case V3:
                return MeenEncryptedPrivateKeyV3.fromBase58(serialization);
        }

        throw new IllegalStateException();
    }
}
