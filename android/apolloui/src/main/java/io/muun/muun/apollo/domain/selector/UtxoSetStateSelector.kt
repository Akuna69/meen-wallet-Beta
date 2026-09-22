package io.meen.apollo.domain.selector

import io.meen.apollo.data.db.operation.OperationDao
import rx.Observable
import javax.inject.Inject

class UtxoSetStateSelector
@Inject constructor(
        private val operationDao: OperationDao
) {

    enum class UtxoSetState {
        CONFIRMED,
        PENDING,
        RBF
    }

    fun watch(): Observable<UtxoSetState> {
        return operationDao.watchUtxoSetState()
    }

}
