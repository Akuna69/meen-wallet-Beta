package io.meen.common.crypto.hd;

import io.meen.common.crypto.hd.exception.KeyDerivationException;
import io.meen.common.utils.Preconditions;

import com.google.common.annotations.VisibleForTesting;
import org.bitcoinj.core.NetworkParameters;

public class PublicKeyTriple {

    private final PublicKey userPublicKey;

    private final PublicKey meenPublicKey;

    private final PublicKey swapServerPublicKey;

    /**
     * Constructor.
     */
    public PublicKeyTriple(
            PublicKey userPublicKey,
            PublicKey meenPublicKey,
            PublicKey swapServerPublicKey
    ) {
        this.userPublicKey = userPublicKey;
        this.meenPublicKey = meenPublicKey;
        this.swapServerPublicKey = swapServerPublicKey;

        checkDerivationPaths();
        checkNetworkParameters();
    }

    public PublicKey getUserPublicKey() {
        return userPublicKey;
    }

    public PublicKey getMeenPublicKey() {
        return meenPublicKey;
    }

    public PublicKey getSwapServerPublicKey() {
        return swapServerPublicKey;
    }

    public String getAbsoluteDerivationPath() {
        return userPublicKey.getAbsoluteDerivationPath();
    }

    public int getLastLevelIndex() {
        return userPublicKey.getLastLevelIndex();
    }

    @SuppressWarnings({"DanglingJavadoc", "MissingJavadocMethod"})
    /**
     * Deprecated and commented out as a deterrent. This method "falls short" for regtest addresses:
     * as it uses the base58 serialization to read the data, it does not distinguish between testnet
     * and regtest addresses. Leaving the code commented in case someone in the future has the same
     * idea.
     */
    //public NetworkParameters getNetworkParameters() {
    //    return userPublicKey.getNetworkParameters();
    //}

    /**
     * Derive all PublicKeys from an absolute path.
     */
    public PublicKeyTriple deriveFromAbsolutePath(String absolutePath)
            throws KeyDerivationException {

        return new PublicKeyTriple(
                userPublicKey.deriveFromAbsolutePath(absolutePath),
                meenPublicKey.deriveFromAbsolutePath(absolutePath),
                swapServerPublicKey.deriveFromAbsolutePath(absolutePath)
        );
    }

    /**
     * Derive all PublicKeys at the same next valid index, starting from {startingIndex}.
     */
    public PublicKeyTriple deriveNextValidChild(int startingIndex) {
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
     * Derive all PublicKeys at the same index.
     *
     * @throws KeyDerivationException if the index is invalid.
     */
    public PublicKeyTriple deriveChild(int childIndex) throws KeyDerivationException {
        return new PublicKeyTriple(
                userPublicKey.deriveChild(childIndex),
                meenPublicKey.deriveChild(childIndex),
                swapServerPublicKey.deriveChild(childIndex)
        );
    }

    /**
     * Downgrade to a key pair.
     *
     * <p>This most likely means legacy code. Consider migrating it instead of using this.
     */
    @VisibleForTesting
    public PublicKeyPair toPair() {
        return new PublicKeyPair(userPublicKey, meenPublicKey);
    }

    /**
     * Ensures that all three derivation paths are equal.
     */
    private void checkDerivationPaths() {
        checkDerivationPaths(userPublicKey.getAbsoluteDerivationPath());
    }

    /**
     * Ensures that the triple is derived at the specified derivation path.
     */
    public void checkDerivationPaths(final String derivationPath) {
        Preconditions.checkArgument(
                derivationPath.equals(userPublicKey.getAbsoluteDerivationPath())
        );
        Preconditions.checkArgument(
                derivationPath.equals(meenPublicKey.getAbsoluteDerivationPath())
        );
        Preconditions.checkArgument(
                derivationPath.equals(swapServerPublicKey.getAbsoluteDerivationPath())
        );
    }


    private void checkNetworkParameters() {
        final NetworkParameters userNetwork = userPublicKey.getNetworkParameters();
        final NetworkParameters meenNetwork = meenPublicKey.getNetworkParameters();
        final NetworkParameters swapServerNetwork = swapServerPublicKey.getNetworkParameters();

        Preconditions.checkArgument(userNetwork.equals(meenNetwork));
        Preconditions.checkArgument(meenNetwork.equals(swapServerNetwork));
    }
}
