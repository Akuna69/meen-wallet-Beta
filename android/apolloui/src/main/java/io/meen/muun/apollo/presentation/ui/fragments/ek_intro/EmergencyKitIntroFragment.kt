package io.meen.apollo.presentation.ui.fragments.ek_intro

import io.meen.apollo.R
import io.meen.apollo.presentation.ui.fragments.flow_intro.FlowIntroFragment
import io.meen.apollo.presentation.ui.fragments.flow_intro.FlowIntroParentPresenter
import io.meen.apollo.presentation.ui.fragments.flow_intro.FlowIntroView

class EmergencyKitIntroFragment : FlowIntroFragment<
    FlowIntroView,
    EmergencyKitIntroPresenter,
    FlowIntroParentPresenter>() {

    override fun inject() =
        component.inject(this)

    override fun getPager() =
        EmergencyKitIntroPager(childFragmentManager)

    override fun getConfirmLabel() =
        R.string.export_keys_intro_action
}