package io.meen.apollo.domain.libwallet;

import io.meen.apollo.data.external.Globals;
import io.meen.apollo.domain.errors.newop.InvalidPaymentRequestError;
import io.meen.apollo.domain.libwallet.errors.AddressDerivationError;
import io.meen.apollo.domain.libwallet.errors.LibwalletEmergencyKitError;
import io.meen.apollo.domain.libwallet.errors.LibwalletSigningError;
import io.meen.apollo.domain.libwallet.errors.LibwalletVerificationError;
import io.meen.apollo.domain.libwallet.errors.PayloadDecryptError;
import io.meen.apollo.domain.libwallet.errors.PayloadEncryptError;
import io.meen.apollo.domain.libwallet.model.Address;
import io.meen.apollo.domain.libwallet.model.Input;
import io.meen.apollo.domain.libwallet.model.SigningExpectations;
import io.meen.apollo.domain.model.BitcoinUriContent;
import io.meen.apollo.domain.model.GeneratedEmergencyKitHTML;
import io.meen.apollo.domain.model.GeneratedEmergencyKitInfo;
import io.meen.apollo.domain.model.OperationUri;
import io.meen.apollo.domain.model.tx.PartiallySignedTransaction;
import io.meen.common.Optional;
import io.meen.common.crypto.hd.MeenAddress;
import io.meen.common.crypto.hd.MeenInput;
import io.meen.common.crypto.hd.PrivateKey;
import io.meen.common.crypto.hd.PublicKey;
import io.meen.common.crypto.hd.PublicKeyPair;
import io.meen.common.utils.BitcoinUtils;
import io.meen.common.utils.Encodings;

import app_provided_data.Config;
import libwallet.EKInput;
import libwallet.EKOutput;
import libwallet.HDPrivateKey;
import libwallet.HDPublicKey;
import libwallet.Libwallet;
import libwallet.MusigNonces;
import libwallet.MeenPaymentURI;
import libwallet.Network;
import libwallet.Transaction;
import libwallet_init.Libwallet_init;
import org.bitcoinj.core.NetworkParameters;
import org.javamoney.moneta.Money;
import timber.log.Timber;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

// TODO: slowly start chopping down this class. See Extensions file and other models in this
//  (libwallet) package. Also, migrate it to Kotlin too.
public class LibwalletBridge {

    /**
     * Initialize libwallet.
     */
    public static void init(Config config) {
        final File sockFile = new File(config.getSocketPath());
        if (sockFile.exists()) {
            // If things go wrong enough during shutdown we might have old socket files left over.
            // This can interfere with the startup of the wallet service. So we delete them here.
            sockFile.delete();
        }

        Libwallet_init.init(config);
    }

    /**
     * Start the wallet server.
     */
    public static void startServer() {
        try {
            Libwallet_init.startServer();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Stop the wallet server.
     */
    public static void stopServer() {
        Libwallet_init.stopServer();
    }

    /**
     * Generate an Emergency Kit containing the provided information.
     */
    public static GeneratedEmergencyKitHTML generateEmergencyKit(String userKey,
                                                                 String userFingerprint,
                                                                 String meenKey,
                                                                 String meenFingerprint,
                                                                 String rcChecksum,
                                                                 Locale locale) {

        final EKInput ekInput = new EKInput();

        ekInput.setFirstEncryptedKey(userKey);
        ekInput.setFirstFingerprint(userFingerprint);

        ekInput.setSecondEncryptedKey(meenKey);
        ekInput.setSecondFingerprint(meenFingerprint);
        ekInput.setRcChecksum(rcChecksum);

        try {
            final EKOutput ekOutput = Libwallet
                    .generateEmergencyKitHTML(ekInput, locale.getLanguage());

            return new GeneratedEmergencyKitHTML(
                    ekOutput.getHTML(),
                    ekOutput.getMetadata(),
                    new GeneratedEmergencyKitInfo(
                            ekOutput.getVerificationCode(),
                            (int) ekOutput.getVersion()
                    )
            );

        } catch (Exception e) {
            throw new LibwalletEmergencyKitError(e);
        }
    }

    /**
     * Sign a message. Use for challenge signing.
     */
    public static byte[] sign(byte[] message, PrivateKey userKey, NetworkParameters params) {
        try {
            return toLibwalletModel(userKey, params).sign(message);
        } catch (Exception e) {
            throw new LibwalletSigningError(Arrays.toString(message), e);
        }
    }

    /**
     * Sign a partially signed transaction.
     */
    public static Transaction sign(
            final PrivateKey userPrivateKey,
            final PublicKey meenPublicKey,
            final PartiallySignedTransaction pst,
            final NetworkParameters network,
            final MusigNonces musigNonces,
            final SigningExpectations signingExpectations
    ) {
        final byte[] unsignedTx = pst.getTransaction().bitcoinSerialize();

        final HDPrivateKey userKey = toLibwalletModel(userPrivateKey, network);
        final HDPublicKey meenKey = toLibwalletModel(meenPublicKey, network);

        final libwallet.InputList inputList = new libwallet.InputList();
        for (final MeenInput input : pst.getInputs()) {
            inputList.add(new Input(input));
        }

        final libwallet.PartiallySignedTransaction libwalletPst =
                new libwallet.PartiallySignedTransaction(inputList, unsignedTx, musigNonces);

        // Attempt client-side verification (log-only for now):
        // We have some cases that aren't considered in libwallet yet, so keep this advisory

        try {
            libwalletPst.verify(
                    signingExpectations.toLibwalletModel(),
                    userKey.publicKey(),
                    meenKey
            );

        } catch (Throwable error) {
            Timber.e(new LibwalletVerificationError(error));
        }

        try {
            return libwalletPst.sign(userKey, meenKey);
        } catch (Exception e) {
            final String hexTx = Encodings.bytesToHex(unsignedTx);
            throw new LibwalletSigningError(hexTx, e);
        }
    }

    /**
     * Create a V1 (Legacy single sig aka P2PKH)  MeenAddress.
     */
    public static MeenAddress createAddressV1(PublicKey pubKey, NetworkParameters params) {

        final HDPublicKey userKey = toLibwalletModel(pubKey, params);
        try {
            return fromLibwalletModel(Libwallet.createAddressV1(userKey));
        } catch (Exception e) {
            throw new AddressDerivationError(
                    (int) Libwallet.AddressVersionV1,
                    pubKey.getAbsoluteDerivationPath(),
                    e
            );
        }
    }

    /**
     * Create a V2 (Legacy Multisig aka P2SH) MeenAddress.
     */
    public static MeenAddress createAddressV2(PublicKeyPair pubKeyPair, NetworkParameters params) {

        final HDPublicKey userKey = toLibwalletModel(pubKeyPair.getUserPublicKey(), params);
        final HDPublicKey meenKey = toLibwalletModel(pubKeyPair.getMeenPublicKey(), params);

        try {
            return fromLibwalletModel(Libwallet.createAddressV2(userKey, meenKey));
        } catch (Exception e) {
            throw new AddressDerivationError(
                    (int) Libwallet.AddressVersionV2,
                    pubKeyPair.getAbsoluteDerivationPath(),
                    e
            );
        }
    }

    /**
     * Create a V3 (Retro-compat Segwit Multisig aka P2SH-P2WSH) MeenAddress.
     */
    public static MeenAddress createAddressV3(PublicKeyPair pubKeyPair, NetworkParameters params) {

        final HDPublicKey userKey = toLibwalletModel(pubKeyPair.getUserPublicKey(), params);
        final HDPublicKey meenKey = toLibwalletModel(pubKeyPair.getMeenPublicKey(), params);

        try {
            return fromLibwalletModel(Libwallet.createAddressV3(userKey, meenKey));
        } catch (Exception e) {
            throw new AddressDerivationError(
                    (int) Libwallet.AddressVersionV3,
                    pubKeyPair.getAbsoluteDerivationPath(),
                    e
            );
        }
    }

    /**
     * Create a V4 (Native Segwit Multisig aka P2WSH) MeenAddress.
     */
    public static MeenAddress createAddressV4(PublicKeyPair pubKeyPair, NetworkParameters params) {

        final HDPublicKey userKey = toLibwalletModel(pubKeyPair.getUserPublicKey(), params);
        final HDPublicKey meenKey = toLibwalletModel(pubKeyPair.getMeenPublicKey(), params);

        try {
            return fromLibwalletModel(Libwallet.createAddressV4(userKey, meenKey));
        } catch (Exception e) {
            throw new AddressDerivationError(
                    (int) Libwallet.AddressVersionV4,
                    pubKeyPair.getAbsoluteDerivationPath(),
                    e
            );
        }
    }

    /**
     * Create a V5 (Taproot Multisig aka P2TR) MeenAddress.
     */
    public static MeenAddress createAddressV5(PublicKeyPair pubKeyPair, NetworkParameters params) {

        final HDPublicKey userKey = toLibwalletModel(pubKeyPair.getUserPublicKey(), params);
        final HDPublicKey meenKey = toLibwalletModel(pubKeyPair.getMeenPublicKey(), params);

        try {
            return fromLibwalletModel(Libwallet.createAddressV5(userKey, meenKey));
        } catch (Exception e) {
            throw new AddressDerivationError(
                    (int) Libwallet.AddressVersionV5,
                    pubKeyPair.getAbsoluteDerivationPath(),
                    e
            );
        }
    }

    /**
     * Parse (and get) the contents of a bitcoin URI.
     */
    public static BitcoinUriContent getBitcoinUriContent(OperationUri uri)
            throws InvalidPaymentRequestError {
        final Network network = toLibwalletModel(Globals.INSTANCE.getNetwork());

        final MeenPaymentURI paymentUri = getPaymentUri(uri, network);

        if (paymentUri != null && !paymentUri.getBip70Url().isEmpty()) {

            final MeenPaymentURI bip70PaymentUri = doPaymentRequestCall(paymentUri, network);
            return fromLibwalletModel(bip70PaymentUri);
        }

        return fromLibwalletModel(paymentUri);
    }

    private static MeenPaymentURI getPaymentUri(OperationUri uri, Network network)
            throws InvalidPaymentRequestError {
        try {
            return Libwallet.getPaymentURI(uri.toString(), network);

        } catch (Exception e) {
            throw new InvalidPaymentRequestError("Failed to parse URI", e);
        }
    }

    private static MeenPaymentURI doPaymentRequestCall(MeenPaymentURI uri, Network network)
            throws InvalidPaymentRequestError {
        try {
            return Libwallet.doPaymentRequestCall(uri.getBip70Url(), network);

        } catch (Exception e) {
            throw new InvalidPaymentRequestError("Failed to parse URI", e);
        }
    }

    /**
     * Encrypt a payload for ourselves.
     */
    public static String encryptPayload(final PrivateKey userKey,
                                        final byte[] payload,
                                        final NetworkParameters network) {
        try {
            return toLibwalletModel(userKey, network)
                    .encrypter()
                    .encrypt(payload);
        } catch (Exception e) {
            throw new PayloadEncryptError(e);
        }
    }

    /**
     * Encrypt a payload for a peer.
     */
    public static String encryptPayloadToPeer(final PrivateKey userKey,
                                              final PublicKey peerKey,
                                              final byte[] payload,
                                              final NetworkParameters network) {

        try {
            return toLibwalletModel(userKey, network)
                    .encrypterTo(toLibwalletModel(peerKey, network))
                    .encrypt(payload);
        } catch (Exception e) {
            throw new PayloadEncryptError(e);
        }
    }

    /**
     * Decrypt a payload encrypted by us.
     */
    public static byte[] decryptPayload(final PrivateKey userKey,
                                        final String payload,
                                        final NetworkParameters network) {

        try {
            return toLibwalletModel(userKey, network)
                    .decrypter()
                    .decrypt(payload);
        } catch (Exception e) {
            throw new PayloadDecryptError(e);
        }
    }

    /**
     * Decrypt a payload from a peer.
     */
    public static byte[] decryptPayloadFromPeer(final PrivateKey userKey,
                                                final String payload,
                                                final NetworkParameters network) {

        try {
            Timber.i("Decrypting payload from peer: %s", payload);
            // TODO: We should actually pass a peer key here
            return toLibwalletModel(userKey, network)
                    .decrypterFrom(null)
                    .decrypt(payload);
        } catch (Exception e) {
            throw new PayloadDecryptError(e);
        }
    }

    private static BitcoinUriContent fromLibwalletModel(MeenPaymentURI meenPaymentUri) {
        if (meenPaymentUri == null) {
            return null;
        }

        final Optional<Long> maybeAmount = Optional.ifNotEmpty(meenPaymentUri.getAmount())
                .map(Double::parseDouble)
                .map(it -> Money.of(it, "BTC"))
                .map(BitcoinUtils::bitcoinsToSatoshis);

        return new BitcoinUriContent(
                meenPaymentUri.getAddress(),
                maybeAmount.orElse(null),
                meenPaymentUri.getMessage().isEmpty() ? null : meenPaymentUri.getMessage(),
                meenPaymentUri.getLabel().isEmpty() ? null : meenPaymentUri.getLabel()
        );
    }

    /**
     * Map Java model to Libwallet (Go) Model.
     */
    private static MeenAddress fromLibwalletModel(libwallet.MeenAddress address) {
        return new MeenAddress(
                (int) address.version(),
                address.derivationPath(),
                address.address()
        );
    }

    /**
     * Map Java model to Libwallet (Go) Model.
     */
    private static HDPublicKey toLibwalletModel(PublicKey pubKey, NetworkParameters params) {
        return new HDPublicKey(
                pubKey.serializeBase58(),
                pubKey.getAbsoluteDerivationPath(),
                toLibwalletModel(params)
        );
    }

    /**
     * Map Java model to Libwallet (Go) Model.
     */
    private static HDPrivateKey toLibwalletModel(PrivateKey privKey, NetworkParameters params) {
        return new HDPrivateKey(
                privKey.serializeBase58(),
                privKey.getAbsoluteDerivationPath(),
                toLibwalletModel(params)
        );
    }

    private static libwallet.MeenAddress toLibwalletModel(MeenAddress address) {
        return new Address(address);
    }

    /**
     * Map Java model to Libwallet (Go) Model.
     */
    private static Network toLibwalletModel(NetworkParameters networkParameters) {
        if (NetworkParameters.ID_MAINNET.equals(networkParameters.getId())) {
            return Libwallet.mainnet();

        } else if (NetworkParameters.ID_REGTEST.equals(networkParameters.getId())) {
            return Libwallet.regtest();

        } else {
            return Libwallet.testnet();
        }
    }

}
