package io.meen.common.api;

import io.meen.common.Supports;
import io.meen.common.utils.Deprecated;
import io.meen.common.utils.Since;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeySet {

    @NotEmpty
    public String encryptedPrivateKey;

    @Nullable // If user has not set up RC
    public String meenKey; // This is the encryptedMeenKey

    @Nullable
    @Since(apolloVersion = Supports.Fingerprint.APOLLO, falconVersion = Supports.Fingerprint.FALCON)
    public String meenKeyFingerprint;

    @Nullable
    @Deprecated(atApolloVersion = 46)
    public Map<String, byte[]> challengePublicKeys;

    @Since(apolloVersion = 40)
    @Nullable
    public List<ChallengeKeyJson> challengeKeys;

    /**
     * Json constructor.
     */
    public KeySet() {
    }

    /**
     * Houston constructor.
     */
    public KeySet(String encryptedPrivateKey,
                  @Nullable String meenKey,
                  @Nullable String meenKeyFingerprint,
                  @Nullable Map<String, byte[]> challengePublicKeys,
                  @Nullable List<ChallengeKeyJson> challengeKeys) {

        this.encryptedPrivateKey = encryptedPrivateKey;
        this.meenKey = meenKey;
        this.meenKeyFingerprint = meenKeyFingerprint;
        this.challengePublicKeys = challengePublicKeys;
        this.challengeKeys = challengeKeys;
    }
}
