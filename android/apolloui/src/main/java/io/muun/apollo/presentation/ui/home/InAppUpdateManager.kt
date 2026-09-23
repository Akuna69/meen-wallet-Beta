package io.muun.apollo.presentation.ui.home

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity

interface InAppUpdateManager {

    fun checkForUpdate()

    fun interface Factory {
        operator fun invoke(
            activity: AppCompatActivity,
            updateLauncher: ActivityResultLauncher<IntentSenderRequest>
        ): InAppUpdateManager
    }
}
