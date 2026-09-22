package io.meen.apollo.presentation.ui.fragments.ek_verify

import io.meen.apollo.domain.model.GeneratedEmergencyKitInfo
import io.meen.apollo.presentation.ui.base.ParentPresenter

interface EmergencyKitVerifyParentPresenter: ParentPresenter {

    fun getGeneratedEmergencyKit(): GeneratedEmergencyKitInfo

    fun refreshToolbar()

    fun confirmEmergencyKitVerify()

    fun showEmergencyKitVerifyHelp()

    fun cancelEmergencyKitVerify()
}