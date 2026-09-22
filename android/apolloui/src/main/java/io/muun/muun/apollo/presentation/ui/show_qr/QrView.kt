package io.meen.apollo.presentation.ui.show_qr

import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.view.EditAmountItem

interface QrView : BaseView, EditAmountItem.EditAmountHandler {

    fun setQrContent(displayContent: String, qrContent: String)

    fun toggleAdvancedSettings()

}
