package io.meen.apollo.domain.action.permission

import io.meen.apollo.data.preferences.permission.NotificationPermissionStateRepository
import io.meen.apollo.domain.model.PermissionState
import javax.inject.Inject

class SetNotificationPermissionNeverAskAgainAction @Inject constructor(
    private val notificationPermissionStateRepository: NotificationPermissionStateRepository,
) {

    fun run() {
        return notificationPermissionStateRepository.store(PermissionState.PERMANENTLY_DENIED)
    }
}