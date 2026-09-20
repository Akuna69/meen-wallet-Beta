package io.meen.apollo.utils.screens

import android.content.Context
import androidx.test.uiautomator.UiDevice
import io.meen.apollo.R
import io.meen.apollo.utils.WithMeenInstrumentationHelpers

class SecurityCenterScreen(
    override val device: UiDevice,
    override val context: Context,
) : WithMeenInstrumentationHelpers {

    fun goToEmailAndPassword() {
        id(R.id.task_email).click()
    }

    fun goToRecoveryCode() {
        id(R.id.task_recovery_code).click()
    }

    fun goToEmergencyKit() {
        id(R.id.task_export_keys).click()
    }
}