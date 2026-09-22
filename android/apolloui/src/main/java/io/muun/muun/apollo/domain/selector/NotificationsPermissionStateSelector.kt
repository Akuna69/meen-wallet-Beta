package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.permission.NotificationPermissionStateRepository
import io.meen.apollo.domain.model.PermissionState
import javax.inject.Inject

class NotificationsPermissionStateSelector @Inject constructor(
    private val notificationPermissionStateRepository: NotificationPermissionStateRepository,
) {

    fun get(): PermissionState =
        notificationPermissionStateRepository.get()
}