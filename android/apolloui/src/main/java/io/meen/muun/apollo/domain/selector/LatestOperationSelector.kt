package io.meen.apollo.domain.selector

import io.meen.apollo.data.db.operation.OperationDao
import io.meen.apollo.domain.model.Operation
import io.meen.common.Optional
import rx.Observable
import javax.inject.Inject

class LatestOperationSelector @Inject constructor(private val operationDao: OperationDao) {

    fun watch(): Observable<Optional<Operation>> {
        return operationDao.fetchMaybeLatest()
                .distinct { maybeOp -> maybeOp.map { it.hid }.orElse(null) }
    }

    fun get(): Optional<Operation> {
        return watch().toBlocking().first()
    }
}