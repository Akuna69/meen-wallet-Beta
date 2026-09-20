package io.meen.apollo.domain.action.notification

import io.meen.apollo.data.preferences.NotificationPermissionSkippedRepository
import javax.inject.Inject

class SetNotificationPermissionSkippedAction @Inject constructor(
    private val notificationPermissionSkippedRepository: NotificationPermissionSkippedRepository,
) {

    fun run() {
        return notificationPermissionSkippedRepository.store(true)
    }
}