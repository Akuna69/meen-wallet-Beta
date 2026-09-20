package io.meen.apollo.presentation.ui.fragments.password_setup_intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.FragmentSetupPasswordIntroBinding
import io.meen.apollo.domain.model.SecurityLevel
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MeenHeader

class SetupPasswordIntroFragment : SingleFragment<SetupPasswordIntroPresenter>(),
    SetupPasswordIntroView {

    private val binding: FragmentSetupPasswordIntroBinding
        get() = getBinding() as FragmentSetupPasswordIntroBinding

    override fun bindingInflater(): (LayoutInflater, ViewGroup, Boolean) -> ViewBinding {
        return FragmentSetupPasswordIntroBinding::inflate
    }

    override fun inject() =
        component.inject(this)

    override fun getLayoutResource() =
        R.layout.fragment_setup_password_intro

    override fun initializeUi(view: View) {
        binding.createEmailSkip.setOnClickListener { presenter.skipEmailSetup() }
        binding.setupPasswordIntroAction.setOnClickListener { presenter.startSetup() }
    }

    override fun setUpHeader() {
        parentActivity.header.let {
            it.setNavigation(MeenHeader.Navigation.BACK)
            it.hideTitle()
            it.setIndicatorText(null)
            it.setElevated(false)
        }
    }

    override fun setSecurityLevel(securityLevel: SecurityLevel) {
        if (securityLevel == SecurityLevel.ANON) {
            binding.createEmailSkip.visibility = View.VISIBLE
        }

        val hasRecoveryCode = securityLevel == SecurityLevel.SKIPPED_EMAIL_RC
            || securityLevel == SecurityLevel.SKIPPED_EMAIL_DONE

        if (hasRecoveryCode) {
            val infoBox = binding.setupPasswordIntroInfoBox
            infoBox.setTitle(R.string.setup_password_intro_title_has_rc)
            infoBox.setDescription(R.string.setup_password_intro_body_has_rc)
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.goBack()
        return true
    }
}