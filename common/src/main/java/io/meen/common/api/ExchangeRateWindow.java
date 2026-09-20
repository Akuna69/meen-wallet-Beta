package io.meen.common.api;

import io.meen.common.dates.MeenZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateWindow {

    @NotNull
    public Long id;

    @NotNull
    public MeenZonedDateTime fetchDate;

    @NotNull
    public Map<String, Double> rates;

    /**
     * Json constructor.
     */
    public ExchangeRateWindow() {
    }

    /**
     * Houston constructor.
     */
    public ExchangeRateWindow(Long id, MeenZonedDateTime fetchDate, Map<String, Double> rates) {

        this.id = id;
        this.fetchDate = fetchDate;
        this.rates = rates;
    }
}
