package io.muun.common.crypto.hd;

import io.muun.common.api.MeenInputSubmarineSwapV102Json;
import io.muun.common.utils.Encodings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Additional details required to spend a MeenInput consuming a SubmarineSwap output V102.
 */
public class MeenInputSubmarineSwapV102 {

    @NotNull
    private byte[] swapPaymentHash256;

    @NotNull
    private byte[] userPublicKey;

    @NotNull
    private byte[] meenPublicKey;

    @NotNull
    private byte[] swapServerPublicKey;

    private int numBlocksForExpiration;

    @Nullable
    private Signature swapServerSignature;

    @Nullable
    private String swapUuid;

    /**
     * Build from a json-serializable representation.
     */
    public static MeenInputSubmarineSwapV102 fromJson(MeenInputSubmarineSwapV102Json json) {

        return new MeenInputSubmarineSwapV102(
                Encodings.hexToBytes(json.swapPaymentHash256Hex),
                Encodings.hexToBytes(json.userPublicKeyHex),
                Encodings.hexToBytes(json.meenPublicKeyHex),
                Encodings.hexToBytes(json.swapServerPublicKeyHex),
                json.numBlocksForExpiration,
                json.swapServerSignature == null
                        ? null
                        : Signature.fromJson(json.swapServerSignature),
                null
        );
    }

    /**
     * Constructor.
     */
    public MeenInputSubmarineSwapV102(
            byte[] swapPaymentHash256,
            byte[] userPublicKey,
            byte[] meenPublicKey,
            byte[] swapServerPublicKey,
            int numBlocksForExpiration,
            @Nullable Signature swapServerSignature,
            @Nullable String swapUuid) {

        this.swapPaymentHash256 = swapPaymentHash256;
        this.userPublicKey = userPublicKey;
        this.meenPublicKey = meenPublicKey;
        this.swapServerPublicKey = swapServerPublicKey;
        this.numBlocksForExpiration = numBlocksForExpiration;
        this.swapServerSignature = swapServerSignature;
        this.swapUuid = swapUuid;
    }

    @Nullable
    public String getSwapUuid() {
        return swapUuid;
    }

    @Nullable
    public Signature getSwapServerSignature() {
        return swapServerSignature;
    }

    public void setSwapServerSignature(@Nullable Signature swapServerSignature) {
        this.swapServerSignature = swapServerSignature;
    }

    /**
     * Convert to a json-serializable representation.
     */
    public MeenInputSubmarineSwapV102Json toJson() {

        return new MeenInputSubmarineSwapV102Json(
                Encodings.bytesToHex(swapPaymentHash256),
                Encodings.bytesToHex(userPublicKey),
                Encodings.bytesToHex(meenPublicKey),
                Encodings.bytesToHex(swapServerPublicKey),
                numBlocksForExpiration,
                swapServerSignature == null ? null : swapServerSignature.toJson()
        );
    }

    public byte[] getSwapPaymentHash256() {
        return swapPaymentHash256;
    }

    public byte[] getUserPublicKey() {
        return userPublicKey;
    }

    public byte[] getMeenPublicKey() {
        return meenPublicKey;
    }

    public byte[] getSwapServerPublicKey() {
        return swapServerPublicKey;
    }

    public int getNumBlocksForExpiration() {
        return numBlocksForExpiration;
    }
}
