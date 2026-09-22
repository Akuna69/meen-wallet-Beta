package io.meen.common.api;

import io.meen.common.Supports;
import io.meen.common.utils.Since;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetupChallengeResponse {

    @Nullable
    @JsonProperty("meenKey")
    public String meenKey;

    @Nullable
    @Since(apolloVersion = Supports.Fingerprint.APOLLO, falconVersion = Supports.Fingerprint.FALCON)
    public String meenKeyFingerprint;

    /**
     * Json constructor.
     */
    public SetupChallengeResponse() {
    }
}
