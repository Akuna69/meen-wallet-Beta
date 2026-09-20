package io.meen.apollo.presentation.ui.taproot_setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.TaprootSetupActivityBinding
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity
import io.meen.apollo.presentation.ui.fragments.ek_save.EmergencyKitSaveFragment
import io.meen.apollo.presentation.ui.fragments.ek_verify.EmergencyKitVerifyFragment
import io.meen.apollo.presentation.ui.fragments.ek_verify_cloud.EmergencyKitCloudVerifyFragment
import io.meen.apollo.presentation.ui.fragments.ek_verify_help.EmergencyKitVerifyHelpFragment
import io.meen.apollo.presentation.ui.fragments.tr_intro.TaprootIntroFragment
import io.meen.apollo.presentation.ui.fragments.tr_success.TaprootSuccessFragment
import io.meen.apollo.presentation.ui.view.MuunHeader
import io.meen.apollo.presentation.ui.view.MuunHeader.Navigation

class TaprootSetupActivity : SingleFragmentActivity<TaprootSetupPresenter>(), TaprootSetupView {

    companion object {
        fun getStartActivityIntent(context: Context): Intent {
            return Intent(context, TaprootSetupActivity::class.java)
        }
    }

    private val binding: TaprootSetupActivityBinding
        get() = getBinding() as TaprootSetupActivityBinding

    private val headerView: MuunHeader
        get() = binding.header

    override fun inject() {
        component.inject(this)
    }

    override fun isPresenterPersistent() =
        true

    override fun getLayoutResource() =
        R.layout.taproot_setup_activity

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return TaprootSetupActivityBinding::inflate
    }

    override fun getFragmentsContainer() =
        R.id.fragment_container

    override fun getInitialFragment() =
        createStepFragment(TaprootSetupStep.INTRO)

    override fun getHeader() =
        headerView

    override fun initializeUi() {
        super.initializeUi()

        headerView.attachToActivity(this)
        header.setNavigation(Navigation.EXIT)
        header.hideTitle()
        header.setElevated(false)
    }

    override fun goToStep(step: TaprootSetupStep, args: Bundle) {
        if (step == TaprootSetupStep.INTRO) {
            header.setNavigation(Navigation.EXIT)
        } else {
            header.setNavigation(Navigation.BACK)
        }

        val fragment = createStepFragment(step)
        fragment.argumentsBundle.putAll(args)

        replaceFragment(fragment, false)
    }

    private fun createStepFragment(step: TaprootSetupStep): SingleFragment<*> {
        return when (step) {
            TaprootSetupStep.INTRO -> TaprootIntroFragment()
            TaprootSetupStep.SAVE -> EmergencyKitSaveFragment.createForUpdate()
            TaprootSetupStep.VERIFY -> EmergencyKitVerifyFragment()
            TaprootSetupStep.VERIFY_HELP -> EmergencyKitVerifyHelpFragment()
            TaprootSetupStep.CLOUD_VERIFY -> EmergencyKitCloudVerifyFragment()
            TaprootSetupStep.SUCCESS -> TaprootSuccessFragment()
        }
    }
}