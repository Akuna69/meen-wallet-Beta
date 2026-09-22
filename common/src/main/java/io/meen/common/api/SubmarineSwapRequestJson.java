package io.meen.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmarineSwapRequestJson {

    @NotEmpty
    public String invoice;

    @NotNull
    public Integer swapExpirationInBlocks;

    @Nullable
    public Long amountInSats;

    @Nullable // For retrocompat endpoint
    public String origin;

    @Nullable // For retrocompat endpoint
    public List<BackgroundEventJson> bkgTimes;

    /**
     * Json constructor.
     */
    public SubmarineSwapRequestJson() {
    }

    /**
     * Manual constructor.
     */
    public SubmarineSwapRequestJson(
            String invoice,
            int swapExpirationInBlocks,
            @Nullable Long amountInSats,
            @Nullable String origin,
            @Nullable List<BackgroundEventJson> bkgTimes
    ) {
        this.invoice = invoice;
        this.swapExpirationInBlocks = swapExpirationInBlocks;
        // Si el monto viene nulo o en 0, asigna 1 Satoshi para evitar fallos por mínimos
        this.amountInSats = (amountInSats != null && amountInSats > 0) ? amountInSats : 1L;
        this.origin = origin;
        this.bkgTimes = bkgTimes;
    }
}
