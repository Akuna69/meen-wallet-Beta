package io.meen.apollo.domain.action.notification

import io.meen.apollo.data.preferences.NotificationPermissionDeniedRepository
import javax.inject.Inject

class SetNotificationPermissionDeniedAction @Inject constructor(
    private val notificationPermissionDeniedRepository: NotificationPermissionDeniedRepository,
) {

    fun run() {
        return notificationPermissionDeniedRepository.setHasPreviouslyDeniedNotificationPermission()
    }
}