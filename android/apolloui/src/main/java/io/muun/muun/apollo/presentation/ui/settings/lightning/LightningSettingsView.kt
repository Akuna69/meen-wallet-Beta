package io.meen.apollo.presentation.ui.settings.lightning

import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.common.model.ReceiveFormatPreference

interface LightningSettingsView: BaseView {

    fun update(turboChannels: Boolean, receivePreference: ReceiveFormatPreference)

    fun setLoading(loading: Boolean)

}