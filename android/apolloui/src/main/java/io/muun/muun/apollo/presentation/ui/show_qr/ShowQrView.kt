package io.meen.apollo.presentation.ui.show_qr

import android.os.Build
import androidx.annotation.RequiresApi
import io.meen.apollo.presentation.ui.base.BaseView

interface ShowQrView : BaseView {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission()

    fun handleNotificationPermissionGranted()

    fun handleNotificationPermissionPromptWhenPermanentlyDenied()
}
