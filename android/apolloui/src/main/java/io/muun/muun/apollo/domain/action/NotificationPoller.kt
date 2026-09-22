package io.meen.apollo.domain.action

import rx.Observable

interface NotificationPoller {

    /**
     * Pull the latest notifications from Houston.
     */
    fun pullNotifications(): Observable<Void>
}