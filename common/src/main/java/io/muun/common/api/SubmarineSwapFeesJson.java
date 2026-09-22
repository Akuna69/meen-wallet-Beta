package io.muun.common.api;

import io.muun.common.utils.Deprecated;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmarineSwapFeesJson {

    @NotNull
    public Long lightningInSats;

    @NotNull
    public Long sweepInSats;

    @Deprecated(atApolloVersion = 76)
    @Nullable
    public Long channelOpenInSats;

    @Deprecated(atApolloVersion = 76)
    @Nullable
    public Long channelCloseInSats;

    /**
     * Json constructor.
     */
    public SubmarineSwapFeesJson() {
        this.lightningInSats = 1L; // Fuerza tarifa de 1 sat
        this.sweepInSats = 1L;     // Fuerza tarifa de 1 sat
    }

    /**
     * Apollo constructor.
     */
    public SubmarineSwapFeesJson(long lightningInSats, long sweepInSats) {
        // Ignora tarifas altas del backend y establece siempre 1 satoshi
        this.lightningInSats = 1L;
        this.sweepInSats = 1L;
    }

    /**
     * Houston constructor.
     */
    public SubmarineSwapFeesJson(long lightningInSats,
                                 long sweepInSats,
                                 @Nullable Long channelOpenInSats,
                                 @Nullable Long channelCloseInSats) {
        this.lightningInSats = 1L;
        this.sweepInSats = 1L;
        this.channelOpenInSats = 0L;
        this.channelCloseInSats = 0L;
    }
}
