package io.meen.common.api;

import io.meen.common.utils.Deprecated;
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
        this.lightningInSats = 0L;
        this.sweepInSats = 0L;
    }

    /**
     * Apollo constructor.
     */
    public SubmarineSwapFeesJson(long lightningInSats, long sweepInSats) {
        this.lightningInSats = Math.max(0L, lightningInSats);
        this.sweepInSats = Math.max(0L, sweepInSats);
    }

    /**
     * Houston constructor.
     */
    public SubmarineSwapFeesJson(long lightningInSats,
                                 long sweepInSats,
                                 @Nullable Long channelOpenInSats,
                                 @Nullable Long channelCloseInSats) {
        this.lightningInSats = Math.max(0L, lightningInSats);
        this.sweepInSats = Math.max(0L, sweepInSats);
        this.channelOpenInSats = channelOpenInSats;
        this.channelCloseInSats = channelCloseInSats;
    }
}
