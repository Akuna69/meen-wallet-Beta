package io.meen.apollo.domain.model;

import java.util.List;
import javax.validation.constraints.NotNull;

public class RealTimeData {

    @NotNull
    public final FeeWindow feeWindow;

    @NotNull
    public final ExchangeRateWindow exchangeRateWindow;

    @NotNull
    public final int currentBlockchainHeight;

    @NotNull
    public final List<ForwardingPolicy> forwardingPolicies;

    @NotNull
    public final double minFeeRateInWeightUnits;

    @NotNull
    public final List<MeenFeature> features;

    /**
     * Constructor.
     */
    public RealTimeData(
            final FeeWindow feeWindow,
            final ExchangeRateWindow exchangeRateWindow,
            final int currentBlockchainHeight,
            final List<ForwardingPolicy> forwardingPolicies,
            final double minFeeRateInWeightUnits,
            final List<MeenFeature> features
    ) {
        this.feeWindow = feeWindow;
        this.exchangeRateWindow = exchangeRateWindow;
        this.currentBlockchainHeight = currentBlockchainHeight;
        this.forwardingPolicies = forwardingPolicies;
        this.minFeeRateInWeightUnits = minFeeRateInWeightUnits;
        this.features = features;
    }
}
