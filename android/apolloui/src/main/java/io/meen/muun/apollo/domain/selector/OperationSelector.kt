package io.meen.apollo.domain.selector

import io.meen.apollo.data.db.operation.OperationDao
import io.meen.apollo.domain.model.Operation
import rx.Observable
import javax.inject.Inject

class OperationSelector @Inject constructor(
    val operationDao: OperationDao
) {

    fun watch(): Observable<List<Operation>> =
        operationDao.fetchAll()

    fun watchUnsettled(): Observable<List<Operation>> =
        operationDao.fetchUnsettled()

    fun fetchByHId(hid: Long): Operation =
        operationDao.fetchByHid(hid).toBlocking().first()
}