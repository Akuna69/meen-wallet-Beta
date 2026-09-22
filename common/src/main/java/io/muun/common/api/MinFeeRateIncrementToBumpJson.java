package io.muun.common.api;

import io.muun.common.dates.MeenZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinFeeRateIncrementToBumpJson {

    @NotNull
    public Double incrementInSatsPerVbyte;

    @NotNull
    public MeenZonedDateTime createdAt;

    /**
     * Constructor.
     */
    public MinFeeRateIncrementToBumpJson(Double incrementInSatsPerVbyte,
                                         MeenZonedDateTime createdAt) {

        this.incrementInSatsPerVbyte = incrementInSatsPerVbyte;
        this.createdAt = createdAt;
    }

    /**
     * JSON constructor.
     */
    public MinFeeRateIncrementToBumpJson() {
    }
}
