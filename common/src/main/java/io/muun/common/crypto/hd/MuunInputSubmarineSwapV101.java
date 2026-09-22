package io.muun.common.crypto.hd;

import io.muun.common.api.MeenInputSubmarineSwapV101Json;
import io.muun.common.utils.Encodings;

import javax.validation.constraints.NotNull;

/**
 * Additional details required to spend a MeenInput consuming a SubmarineSwap output V101.
 */
public class MeenInputSubmarineSwapV101 {

    /**
     * Build from a json-serializable representation.
     */
    public static MeenInputSubmarineSwapV101 fromJson(MeenInputSubmarineSwapV101Json json) {
        return new MeenInputSubmarineSwapV101(
                json.refundAddress,
                Encodings.hexToBytes(json.swapPaymentHash256Hex),
                Encodings.hexToBytes(json.swapServerPublicKeyHex),
                json.lockTime
        );
    }

    @NotNull
    private final String refundAddress;

    @NotNull
    private final byte[] swapPaymentHash256;

    @NotNull
    private final byte[] swapServerPublicKey;

    @NotNull
    private final long lockTime;

    /**
     * Constructor.
     */
    public MeenInputSubmarineSwapV101(String refundAddress,
                                      byte[] swapPaymentHash256,
                                      byte[] swapServerPublicKey,
                                      long lockTime) {

        this.refundAddress = refundAddress;
        this.swapPaymentHash256 = swapPaymentHash256;
        this.swapServerPublicKey = swapServerPublicKey;
        this.lockTime = lockTime;
    }

    public String getRefundAddress() {
        return refundAddress;
    }

    public byte[] getSwapPaymentHash256() {
        return swapPaymentHash256;
    }

    public byte[] getSwapServerPublicKey() {
        return swapServerPublicKey;
    }

    public long getLockTime() {
        return lockTime;
    }

    /**
     * Convert to a json-serializable representation.
     */
    public MeenInputSubmarineSwapV101Json toJson() {
        return new MeenInputSubmarineSwapV101Json(
                refundAddress,
                Encodings.bytesToHex(swapPaymentHash256),
                Encodings.bytesToHex(swapServerPublicKey),
                lockTime
        );
    }
}
