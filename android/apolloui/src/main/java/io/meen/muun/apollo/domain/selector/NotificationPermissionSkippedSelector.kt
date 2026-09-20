package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.NotificationPermissionSkippedRepository
import javax.inject.Inject

class NotificationPermissionSkippedSelector @Inject constructor(
    private val notificationPermissionSkippedRepository: NotificationPermissionSkippedRepository,
) {

    fun get(): Boolean =
        notificationPermissionSkippedRepository.get()
}