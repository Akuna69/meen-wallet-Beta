package io.meen.apollo.domain.selector

import io.meen.apollo.data.db.contact.ContactDao
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.ContactActions
import io.meen.apollo.domain.model.P2PState
import rx.Observable
import javax.inject.Inject


class P2PStateSelector @Inject constructor(
    private val contactActions: ContactActions,
    private val contactDao: ContactDao,
    private val userRepository: UserRepository
) {

    fun watch(): Observable<P2PState> =
        Observable
            .combineLatest(
                userRepository.fetch(),
                userRepository.watchContactsPermissionState(),
                contactActions.initialSyncPhoneContactsAction.state, // TODO: move to own action
                contactDao.fetchAll(),
                ::P2PState
            )

    fun get(): P2PState =
        watch().toBlocking().first()
}