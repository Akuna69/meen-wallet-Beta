package io.meen.apollo.domain.action.realtime

import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.libwallet.FeeBumpFunctionsProvider
import io.meen.apollo.domain.model.feebump.FeeBumpRefreshPolicy
import io.meen.apollo.domain.utils.toVoid
import rx.Observable
import javax.inject.Inject

/**
 * Update fees data, such as fee window and fee bump functions.
 */

class PreloadFeeDataAction @Inject constructor(
    private val syncRealTimeFees: SyncRealTimeFees,
    private val feeBumpFunctionsProvider: FeeBumpFunctionsProvider,
) : BaseAsyncAction1<FeeBumpRefreshPolicy, Void>() {

    /**
     * Force re-fetch of Houston's RealTimeFees, bypassing throttling logic.
     */
    fun runForced(refreshPolicy: FeeBumpRefreshPolicy): Observable<Void> {
        return syncRealTimeFees.sync(refreshPolicy)
    }

    fun runIfDataIsInvalidated(refreshPolicy: FeeBumpRefreshPolicy) {
        super.run(Observable.defer {
            if (feeBumpFunctionsProvider.areFeeBumpFunctionsInvalidated()) {
                return@defer syncRealTimeFees.sync(refreshPolicy)
            } else {
                return@defer Observable.just(null)
            }
        })
    }

    override fun action(t: FeeBumpRefreshPolicy): Observable<Void> {
        return Observable.defer {
            if (syncRealTimeFees.shouldUpdateData()) {
                return@defer syncRealTimeFees.sync(t)
            } else {
                return@defer Observable.just(null).toVoid()
            }
        }
    }
}