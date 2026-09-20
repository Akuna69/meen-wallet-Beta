package io.meen.apollo.domain.action

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.meen.apollo.BaseTest
import io.meen.apollo.TestUtils
import io.meen.apollo.data.external.Gen
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.FeeWindowRepository
import io.meen.apollo.data.preferences.MinFeeRateRepository
import io.meen.apollo.data.preferences.TransactionSizeRepository
import io.meen.apollo.domain.action.realtime.SyncRealTimeFees
import io.meen.apollo.domain.libwallet.FeeBumpFunctionsProvider
import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.domain.model.RealTimeFees
import io.meen.apollo.domain.model.feebump.FeeBumpRefreshPolicy
import io.meen.apollo.domain.selector.FeatureSelector
import io.meen.common.model.SizeForAmount
import io.meen.common.model.UtxoStatus
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import rx.Observable

class SyncRealTimeFeesTest: BaseTest() {

    private val feeWindowRepository = mockk<FeeWindowRepository>(relaxed = true)
    private val minFeeRateRepository = mockk<MinFeeRateRepository>(relaxed = true)
    private val transactionSizeRepository = mockk<TransactionSizeRepository>(relaxed = true)
    private val houstonClient = mockk<HoustonClient>()
    private val featureSelector = mockk<FeatureSelector>(relaxed = true)
    private val feeBumpFunctionsProvider = mockk<FeeBumpFunctionsProvider>(relaxed = true)

    private lateinit var syncRealTimeFees: SyncRealTimeFees

    val feeBumpFunctions = Gen.feeBumpFunctions()
    val feeWindow = Gen.feeWindow()
    val minFeeRateInWeightUnits = 0.25
    val minMempoolFeeRateInSatPerVbyte = minFeeRateInWeightUnits * 4
    val minFeeRateIncrementToReplaceByFeeInSatPerVbyte = 2.5

    val realTimeFees = RealTimeFees(
        feeBumpFunctions,
        feeWindow,
        minMempoolFeeRateInSatPerVbyte,
        minFeeRateIncrementToReplaceByFeeInSatPerVbyte,
        ZonedDateTime.now()
    )

    private val sizeProgressionWithUnconfirmedUtxos = SizeForAmount(
        1000L,
        240,
        "default:0",
        UtxoStatus.UNCONFIRMED,
        240,
        "m/schema:1'/recovery:1'",
        1
    )

    @Before
    fun setUp() {
        syncRealTimeFees = SyncRealTimeFees(
            houstonClient,
            feeWindowRepository,
            minFeeRateRepository,
            transactionSizeRepository,
            featureSelector,
            feeBumpFunctionsProvider
        )
    }

    @Test
    fun testSyncShouldUpdateRepositories() {
        every { houstonClient.fetchRealTimeFees(any(), any()) }
            .returns(Observable.just(realTimeFees))
        every { transactionSizeRepository.nextTransactionSize }
            .returns(Gen.nextTransactionSize(sizeProgressionWithUnconfirmedUtxos))
        every { featureSelector.get(MeenFeature.EFFECTIVE_FEES_CALCULATION) }
            .returns(true)

        TestUtils.fetchItemFromObservable(syncRealTimeFees.sync(FeeBumpRefreshPolicy.PERIODIC))

        verify(exactly = 1) { feeWindowRepository.store(feeWindow) }
        verify(exactly = 1) { minFeeRateRepository.store(minFeeRateInWeightUnits) }
        verify(exactly = 1) { feeBumpFunctionsProvider.persistFeeBumpFunctions(
            feeBumpFunctions,
            FeeBumpRefreshPolicy.PERIODIC
        ) }
    }

    @Test
    fun testShouldUpdateDataShouldReturnsFalseAfterASync() {
        every { houstonClient.fetchRealTimeFees(any(), any()) }
            .returns(Observable.just(realTimeFees))
        every { transactionSizeRepository.nextTransactionSize }
            .returns(Gen.nextTransactionSize(sizeProgressionWithUnconfirmedUtxos))
        every { featureSelector.get(MeenFeature.EFFECTIVE_FEES_CALCULATION) }
            .returns(true)

        assertTrue(syncRealTimeFees.shouldUpdateData())

        TestUtils.fetchItemFromObservable(syncRealTimeFees.sync(FeeBumpRefreshPolicy.PERIODIC))

        assertFalse(syncRealTimeFees.shouldUpdateData())
    }
}