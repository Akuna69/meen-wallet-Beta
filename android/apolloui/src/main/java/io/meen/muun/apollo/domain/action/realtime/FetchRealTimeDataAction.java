package io.meen.apollo.domain.action.realtime;

import io.meen.apollo.data.net.HoustonClient;
import io.meen.apollo.data.preferences.BlockchainHeightRepository;
import io.meen.apollo.data.preferences.ExchangeRateWindowRepository;
import io.meen.apollo.data.preferences.FeaturesRepository;
import io.meen.apollo.data.preferences.FeeWindowRepository;
import io.meen.apollo.data.preferences.ForwardingPoliciesRepository;
import io.meen.apollo.data.preferences.MinFeeRateRepository;
import io.meen.apollo.domain.action.base.BaseAsyncAction0;
import io.meen.apollo.domain.model.MuunFeature;
import io.meen.common.rx.RxHelper;

import rx.Observable;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FetchRealTimeDataAction extends BaseAsyncAction0<Void> {

    private final HoustonClient houstonClient;

    private final FeeWindowRepository feeWindowRepository;

    private final ExchangeRateWindowRepository exchangeRateWindowRepository;

    private final BlockchainHeightRepository blockchainHeightRepository;

    private final ForwardingPoliciesRepository forwardingPoliciesRepository;

    private final MinFeeRateRepository minFeeRateRepository;

    private final FeaturesRepository featuresRepository;

    /**
     * Update time-sensitive data, such as network fees and exchange rates.
     */
    @Inject
    public FetchRealTimeDataAction(
            final HoustonClient houstonClient,
            final FeeWindowRepository feeWindowRepository,
            final ExchangeRateWindowRepository exchangeRateWindowRepository,
            final BlockchainHeightRepository blockchainHeightRepository,
            final ForwardingPoliciesRepository forwardingPoliciesRepository,
            final MinFeeRateRepository minFeeRateRepository,
            final FeaturesRepository featuresRepository
    ) {

        this.houstonClient = houstonClient;
        this.feeWindowRepository = feeWindowRepository;
        this.exchangeRateWindowRepository = exchangeRateWindowRepository;
        this.blockchainHeightRepository = blockchainHeightRepository;
        this.forwardingPoliciesRepository = forwardingPoliciesRepository;
        this.minFeeRateRepository = minFeeRateRepository;
        this.featuresRepository = featuresRepository;
    }

    /**
     * Force re-fetch of Houston's RealTimeData, bypassing any local cache logic.
     */
    public void runForced() {
        super.run(Observable.defer(this::forceSyncRealTimeData));
    }

    @Override
    public Observable<Void> action() {
        return Observable.defer(() -> {
            if (shouldSync()) {
                return forceSyncRealTimeData();

            } else {
                return Observable.just(null);
            }
        });
    }

    private Observable<Void> forceSyncRealTimeData() {
        Timber.d("[Sync] Updating fee/rates");

        return houstonClient.fetchRealTimeData()
                .doOnNext(realTimeData -> {
                    Timber.d("[Sync] Saving updated fee/rates");
                    exchangeRateWindowRepository.storeLatest(realTimeData.exchangeRateWindow);
                    blockchainHeightRepository.store(realTimeData.currentBlockchainHeight);
                    forwardingPoliciesRepository.store(realTimeData.forwardingPolicies);
                    featuresRepository.store(realTimeData.features);

                    // When the FF is ON, this data will be stored by FetchRealTimeFeesAction
                    if (!realTimeData.features.contains(MuunFeature.EFFECTIVE_FEES_CALCULATION)) {
                        feeWindowRepository.store(realTimeData.feeWindow);
                        minFeeRateRepository.store(realTimeData.minFeeRateInWeightUnits);
                    }
                })
                .map(RxHelper::toVoid);
    }

    private boolean shouldSync() {
        final boolean isFeeRecent = feeWindowRepository.isFeeRecent();

        final boolean isExchangeRateRecent = exchangeRateWindowRepository.isSet()
                && exchangeRateWindowRepository.fetchOne().isRecent();

        return (!isFeeRecent || !isExchangeRateRecent);
    }
}
