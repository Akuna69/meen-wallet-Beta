package io.meen.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeenInputJson {

    @NotNull
    public MeenOutputJson prevOut;

    @NotNull
    public MeenAddressJson address;

    @Nullable
    public SignatureJson userSignature;

    @Nullable
    public SignatureJson meenSignature;

    @Nullable
    public SignatureJson swapServerSignature;

    @Nullable
    public MeenInputSubmarineSwapV101Json submarineSwap;

    @Nullable
    public MeenInputSubmarineSwapV102Json submarineSwapV102;

    @Nullable
    public MeenInputIncomingSwapJson incomingSwap;

    @Nullable
    public String rawMeenPublicNonceHex;

    /**
     * Json constructor.
     */
    public MeenInputJson() {
    }

    /**
     * Manual constructor.
     */
    public MeenInputJson(MeenOutputJson prevOut,
                         MeenAddressJson address,
                         @Nullable SignatureJson userSignature,
                         @Nullable SignatureJson meenSignature,
                         @Nullable SignatureJson swapServerSignature,
                         @Nullable MeenInputSubmarineSwapV101Json submarineSwap,
                         @Nullable MeenInputSubmarineSwapV102Json submarineSwapV102,
                         @Nullable MeenInputIncomingSwapJson incomingSwap,
                         @Nullable String rawMeenPublicNonceHex) {

        this.prevOut = prevOut;
        this.address = address;
        this.userSignature = userSignature;
        this.meenSignature = meenSignature;
        this.swapServerSignature = swapServerSignature;
        this.submarineSwap = submarineSwap;
        this.submarineSwapV102 = submarineSwapV102;
        this.incomingSwap = incomingSwap;
        this.rawMeenPublicNonceHex = rawMeenPublicNonceHex;
    }
}
