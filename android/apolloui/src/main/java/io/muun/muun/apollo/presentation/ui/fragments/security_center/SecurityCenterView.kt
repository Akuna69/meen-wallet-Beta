package io.meen.apollo.presentation.ui.fragments.security_center

import io.meen.apollo.domain.model.SecurityCenter
import io.meen.apollo.presentation.ui.base.BaseView


interface SecurityCenterView: BaseView {

    enum class TaskStatus {
        BLOCKED,
        PENDING,
        DONE
    }

    fun setTaskStatus(
        emailStatus: TaskStatus,
        recoveryCodeStatus: TaskStatus,
        exportKeysStatus: TaskStatus,
        securityCenter: SecurityCenter
    )
}