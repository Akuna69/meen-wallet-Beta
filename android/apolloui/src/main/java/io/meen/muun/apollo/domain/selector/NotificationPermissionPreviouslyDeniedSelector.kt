package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.NotificationPermissionDeniedRepository
import javax.inject.Inject

class NotificationPermissionPreviouslyDeniedSelector @Inject constructor(
    private val notificationPermissionDeniedRepository: NotificationPermissionDeniedRepository,
) {

    fun get(): Boolean =
        notificationPermissionDeniedRepository.hasPreviouslyDeniedNotificationPermission()
}