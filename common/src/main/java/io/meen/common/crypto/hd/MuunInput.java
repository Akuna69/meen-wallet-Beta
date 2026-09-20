package io.meen.common.crypto.hd;


import io.meen.common.api.MeenInputJson;
import io.meen.common.utils.Encodings;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class MeenInput {

    @NotNull
    private final MeenOutput prevOut;

    @NotNull
    private final MeenAddress address;

    @Nullable
    private Signature userSignature;

    @Nullable
    private Signature meenSignature; // co-signed inputs only

    @Nullable
    private Signature swapServerSignature; // channel inputs only

    @Nullable
    private MeenInputSubmarineSwapV101 submarineSwap; // submarine swap V101 refund inputs only

    @Nullable
    private MeenInputSubmarineSwapV102 submarineSwapV102; // submarine swap V102 refund inputs only

    @Nullable
    private MeenInputIncomingSwap incomingSwap; // for incoming swap inputs only

    @Nullable
    private byte[] rawUserPublicNonce; // musig inputs only. Set by user.

    @Nullable
    private byte[] rawMeenPublicNonce; // musig inputs only. Set by houston.

    // NOTE: exists only for testing capabilities. DO NOT EVER assume its existence or try to use it
    @Nullable
    private byte[] userSessionId; // musig inputs only

    /**
     * Build from a json-serializable representation.
     */
    public static MeenInput fromJson(MeenInputJson json) {

        return new MeenInput(
                MeenOutput.fromJson(json.prevOut),
                MeenAddress.fromJson(json.address),
                json.userSignature == null ? null : Signature.fromJson(json.userSignature),
                json.meenSignature == null ? null : Signature.fromJson(json.meenSignature),
                json.swapServerSignature == null ? null : Signature.fromJson(
                        json.swapServerSignature
                ),
                json.submarineSwap == null ? null : MeenInputSubmarineSwapV101.fromJson(
                        json.submarineSwap
                ),
                json.submarineSwapV102 == null ? null : MeenInputSubmarineSwapV102.fromJson(
                        json.submarineSwapV102
                ),
                json.incomingSwap == null ? null : MeenInputIncomingSwap.fromJson(
                        json.incomingSwap
                ),
                json.rawMeenPublicNonceHex == null ? null : Encodings.hexToBytes(
                        json.rawMeenPublicNonceHex
                )
        );
    }

    /**
     * Constructor without signatures or additional details.
     */
    public MeenInput(MeenOutput prevOut, MeenAddress address) {
        this.prevOut = prevOut;
        this.address = address;
    }

    /**
     * Full constructor.
     */
    public MeenInput(MeenOutput prevOut,
                     MeenAddress address,
                     @Nullable Signature userSignature,
                     @Nullable Signature meenSignature,
                     @Nullable Signature swapServerSignature,
                     @Nullable MeenInputSubmarineSwapV101 submarineSwap,
                     @Nullable MeenInputSubmarineSwapV102 submarineSwapV102,
                     @Nullable MeenInputIncomingSwap incomingSwap,
                     @Nullable byte[] rawMeenPublicNonce) {

        this.prevOut = prevOut;
        this.address = address;
        this.userSignature = userSignature;
        this.meenSignature = meenSignature;
        this.swapServerSignature = swapServerSignature;
        this.submarineSwap = submarineSwap;
        this.submarineSwapV102 = submarineSwapV102;
        this.incomingSwap = incomingSwap;
        this.rawMeenPublicNonce = rawMeenPublicNonce;
    }

    public MeenOutput getPrevOut() {
        return prevOut;
    }

    public int getVersion() {
        return address.getVersion();
    }

    public String getDerivationPath() {
        return address.getDerivationPath();
    }

    public MeenAddress getAddress() {
        return address;
    }

    @Nullable
    public Signature getUserSignature() {
        return userSignature;
    }

    @Nullable
    public Signature getMeenSignature() {
        return meenSignature;
    }

    @Nullable
    public Signature getSwapServerSignature() {
        return swapServerSignature;
    }

    public void setUserSignature(Signature userSignature) {
        this.userSignature = userSignature;
    }

    public void setMeenSignature(Signature meenSignature) {
        this.meenSignature = meenSignature;
    }

    public void setSwapServerSignature(Signature swapServerSignature) {
        this.swapServerSignature = swapServerSignature;
    }

    @Nullable
    public MeenInputSubmarineSwapV101 getSubmarineSwap() {
        return submarineSwap;
    }

    public void setSubmarineSwap(@Nullable MeenInputSubmarineSwapV101 submarineSwap) {
        this.submarineSwap = submarineSwap;
    }

    @Nullable
    public MeenInputSubmarineSwapV102 getSubmarineSwapV102() {
        return submarineSwapV102;
    }

    public void setSubmarineSwapV102(@Nullable MeenInputSubmarineSwapV102 submarineSwapV102) {
        this.submarineSwapV102 = submarineSwapV102;
    }

    @Nullable
    public MeenInputIncomingSwap getIncomingSwap() {
        return incomingSwap;
    }

    public void setIncomingSwap(@Nullable MeenInputIncomingSwap incomingSwap) {
        this.incomingSwap = incomingSwap;
    }

    @Nullable
    public byte[] getRawUserPublicNonce() {
        return rawUserPublicNonce;
    }

    public void setRawMeenPublicNonce(@Nullable byte[] rawMeenPublicNonce) {
        this.rawMeenPublicNonce = rawMeenPublicNonce;
    }

    @Nullable
    public byte[] getRawMeenPublicNonce() {
        return rawMeenPublicNonce;
    }

    public void setRawUserPublicNonce(@Nullable byte[] rawUserPublicNonce) {
        this.rawUserPublicNonce = rawUserPublicNonce;
    }

    @Nullable
    public byte[] getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(@Nullable byte[] userSessionId) {
        this.userSessionId = userSessionId;
    }

    /**
     * Convert to a json-serializable representation.
     */
    public MeenInputJson toJson() {

        return new MeenInputJson(
                prevOut.toJson(),
                address.toJson(),
                userSignature == null ? null : userSignature.toJson(),
                meenSignature == null ? null : meenSignature.toJson(),
                swapServerSignature == null ? null : swapServerSignature.toJson(),
                submarineSwap == null ? null : submarineSwap.toJson(),
                submarineSwapV102 == null ? null : submarineSwapV102.toJson(),
                incomingSwap == null ? null : incomingSwap.toJson(),
                rawMeenPublicNonce == null ? null : Encodings.bytesToHex(rawMeenPublicNonce)
        );
    }

    public long getLockTime() {
        return submarineSwap != null ? submarineSwap.getLockTime() : 0;
    }
}
