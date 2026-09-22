package io.muun.common.crypto.hd;

import io.muun.common.api.MeenInputIncomingSwapJson;
import io.muun.common.utils.Encodings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Additional details required to spend a MeenInput consuming a SubmarineSwap output V101.
 */
public class MeenInputIncomingSwap {

    @NotNull
    private final byte[] sphinx;

    @NotNull
    private final byte[] htlcTx;

    @NotNull
    private final byte[] swapServerPublicKey;

    @NotNull
    private final byte[] paymentHash256;

    private final long expirationHeight;

    private final long collectInSats;

    @Nullable // Only present if the swap was ever FULFILLED (note: fulfillment tx can be dropped)
    private final String preimageHex;

    private final String htlcOutputKeyPath;

    /**
     * Convert from JSON to model.
     */
    public static MeenInputIncomingSwap fromJson(final MeenInputIncomingSwapJson json) {
        return new MeenInputIncomingSwap(
                Encodings.hexToBytes(json.sphinxHex),
                Encodings.hexToBytes(json.htlcTxHex),
                Encodings.hexToBytes(json.swapServerPublicKeyHex),
                Encodings.hexToBytes(json.paymentHash256Hex),
                json.expirationHeight,
                json.collectInSats,
                json.preimageHex,
                json.htlcOutputKeyPath
        );
    }

    /**
     * Constructor.
     */
    public MeenInputIncomingSwap(
            @Nullable byte[] sphinx,
            byte[] htlcTx,
            byte[] swapServerPublicKey,
            byte[] paymentHash256,
            final long expirationHeight,
            final long collectInSats,
            @Nullable final String preimageHex,
            final String htlcOutputKeyPath
    ) {
        // Due to a few mapping errors, apps expect sphinx to always be non-null. However,
        // Meen to Meen payments don't have a sphinx and swapper now (properly) returns it as null.
        // We map it here for retrocompat with the apps.
        this.sphinx = sphinx != null ? sphinx : new byte[0];
        this.htlcTx = htlcTx;
        this.swapServerPublicKey = swapServerPublicKey;
        this.paymentHash256 = paymentHash256;
        this.expirationHeight = expirationHeight;
        this.collectInSats = collectInSats;
        this.preimageHex = preimageHex;
        this.htlcOutputKeyPath = htlcOutputKeyPath;
    }

    public byte[] getSphinx() {
        return sphinx;
    }

    public byte[] getHtlcTx() {
        return htlcTx;
    }

    public byte[] getSwapServerPublicKey() {
        return swapServerPublicKey;
    }

    public byte[] getPaymentHash256() {
        return paymentHash256;
    }

    public long getExpirationHeight() {
        return expirationHeight;
    }

    public long getCollectInSats() {
        return collectInSats;
    }

    public String getHtlcOutputKeyPath() {
        return htlcOutputKeyPath;
    }

    public String getPreimageHex() {
        return preimageHex;
    }

    /**
     * Convert to JSON.
     */
    public MeenInputIncomingSwapJson toJson() {
        return new MeenInputIncomingSwapJson(
                sphinx,
                htlcTx,
                swapServerPublicKey,
                paymentHash256,
                expirationHeight,
                collectInSats,
                preimageHex,
                htlcOutputKeyPath
        );
    }
}
