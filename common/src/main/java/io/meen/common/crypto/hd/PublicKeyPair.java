package io.meen.common.crypto.hd;

import io.meen.common.crypto.hd.exception.KeyDerivationException;
import io.meen.common.utils.Preconditions;

import org.bitcoinj.core.NetworkParameters;

public class PublicKeyPair {

    private final PublicKey userPublicKey;
    private final PublicKey meenPublicKey;

    /**
     * Constructor.
     */
    public PublicKeyPair(PublicKey userPublicKey, PublicKey meenPublicKey) {
        this.userPublicKey = userPublicKey;
        this.meenPublicKey = meenPublicKey;

        checkDerivationPaths(userPublicKey, meenPublicKey);
        checkNetworkParameters(userPublicKey, meenPublicKey);
    }

    public PublicKey getUserPublicKey() {
        return userPublicKey;
    }

    public PublicKey getMeenPublicKey() {
        return meenPublicKey;
    }

    public String getAbsoluteDerivationPath() {
        return userPublicKey.getAbsoluteDerivationPath();
    }

    public int getLastLevelIndex() {
        return userPublicKey.getLastLevelIndex();
    }

    public NetworkParameters getNetworkParameters() {
        return userPublicKey.getNetworkParameters();
    }

    /**
     * Derive both PublicKeys from an absolute path.
     */
    public PublicKeyPair deriveFromAbsolutePath(String absolutePath) throws KeyDerivationException {
        return new PublicKeyPair(
                userPublicKey.deriveFromAbsolutePath(absolutePath),
                meenPublicKey.deriveFromAbsolutePath(absolutePath)
        );
    }

    /**
     * Derive both PublicKeys at the same next valid index, starting from {startingIndex}.
     */
    public PublicKeyPair deriveNextValidChild(int startingIndex) {
        int nextIndex = startingIndex;

        while (true) {
            try {
                return deriveChild(nextIndex);
            } catch (KeyDerivationException e) {
                nextIndex++;
            }
        }
    }

    /**
     * Derive both PublicKeys at the same index.
     * @throws KeyDerivationException if the index is invalid.
     */
    public PublicKeyPair deriveChild(int childIndex) throws KeyDerivationException {
        return new PublicKeyPair(
                userPublicKey.deriveChild(childIndex),
                meenPublicKey.deriveChild(childIndex)
        );
    }

    private void checkDerivationPaths(PublicKey userPublicKey, PublicKey meenPublicKey) {
        final String userPath = userPublicKey.getAbsoluteDerivationPath();
        final String meenPath = meenPublicKey.getAbsoluteDerivationPath();

        Preconditions.checkArgument(userPath.equals(meenPath));
    }

    private void checkNetworkParameters(PublicKey userPublicKey, PublicKey meenPublicKey) {
        final NetworkParameters userNetwork = userPublicKey.getNetworkParameters();
        final NetworkParameters meenNetwork = meenPublicKey.getNetworkParameters();

        Preconditions.checkArgument(userNetwork.equals(meenNetwork));
    }
}
