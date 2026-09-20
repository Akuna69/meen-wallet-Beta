package io.meen.apollo.presentation.ui.setup_password

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.ActivitySetupPasswordBinding
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.activity.extension.MeenDialog
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity
import io.meen.apollo.presentation.ui.fragments.create_password.CreatePasswordFragment
import io.meen.apollo.presentation.ui.fragments.enter_email.CreateEmailFragment
import io.meen.apollo.presentation.ui.fragments.password_setup_intro.SetupPasswordIntroFragment
import io.meen.apollo.presentation.ui.fragments.setup_password_accept.SetupPasswordAcceptFragment
import io.meen.apollo.presentation.ui.fragments.setup_password_success.SetupPasswordSuccessFragment
import io.meen.apollo.presentation.ui.fragments.verify_email.VerifyEmailFragment
import io.meen.apollo.presentation.ui.view.MeenHeader
import io.meen.apollo.presentation.ui.view.MeenHeader.Navigation

class SetupPasswordActivity : SingleFragmentActivity<SetupPasswordActivityPresenter>(),
    SetupPasswordActivityView {

    companion object {
        fun getStartActivityIntent(context: Context) =
            Intent(context, SetupPasswordActivity::class.java)
    }

    private val binding: ActivitySetupPasswordBinding
        get() = getBinding() as ActivitySetupPasswordBinding

    private val headerView: MeenHeader
        get() = binding.header

    override fun inject() {
        component.inject(this)
    }

    override fun getFragmentsContainer() =
        R.id.container

    override fun getLayoutResource() =
        R.layout.activity_setup_password

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return ActivitySetupPasswordBinding::inflate
    }

    override fun isPresenterPersistent() =
        true

    override fun initializeUi() {
        super.initializeUi()

        headerView.let {
            it.attachToActivity(this)
            it.setNavigation(Navigation.BACK)
        }
    }

    override fun getHeader() =
        headerView

    override fun getInitialFragment() =
        SetupPasswordIntroFragment()

    override fun setUser(user: User) {
        if (user.hasRecoveryCode) {
            headerView.showTitle(R.string.security_center_title_improve_your_security)

        } else {
            headerView.showTitle(R.string.security_center_title_backup_your_wallet)
        }

        headerView.setElevated(true)
    }

    override fun showAbortDialog() {
        MeenDialog.Builder()
            .title(R.string.setup_password_abort_title)
            .message(R.string.setup_password_abort_body)
            .positiveButton(R.string.abort) { presenter.abortPasswordSetup() }
            .negativeButton(R.string.cancel)
            .build()
            .let(this::showDialog)
    }

    override fun showSkipDialog() {
        MeenDialog.Builder()
            .layout(R.layout.dialog_custom_layout)
            .title(R.string.setup_password_skip_title)
            .message(R.string.setup_password_skip_body)
            .positiveButton(R.string.setup_password_skip_yes) { presenter.skipPasswordSetup() }
            .negativeButton(R.string.setup_password_skip_no)
            .build()
            .let(this::showDialog)
    }

    @SuppressLint("StringFormatMatches")
    override fun goToStep(step: SetupPasswordStep) {
        val nextFragment = when (step) {
            SetupPasswordStep.INTRO -> SetupPasswordIntroFragment()
            SetupPasswordStep.CREATE_EMAIL -> CreateEmailFragment()
            SetupPasswordStep.VERIFY_EMAIL -> VerifyEmailFragment()
            SetupPasswordStep.CREATE_PASSWORD -> CreatePasswordFragment()
            SetupPasswordStep.ACCEPT_TERMS -> SetupPasswordAcceptFragment()
            SetupPasswordStep.SUCCESS -> SetupPasswordSuccessFragment()
        }

        val firstNumberedStep = SetupPasswordStep.CREATE_EMAIL.ordinal
        val lastNumberedStep = SetupPasswordStep.ACCEPT_TERMS.ordinal
        val stepNumber = step.ordinal

        val indicatorText = if (stepNumber in firstNumberedStep..lastNumberedStep) {
            getString(R.string.setup_password_step_counter, stepNumber, lastNumberedStep)
        } else {
            ""
        }

        headerView.setIndicatorText(indicatorText)

        val canGoBackToCurrentStep = (step != SetupPasswordStep.VERIFY_EMAIL)
        replaceFragment(nextFragment, canGoBackToCurrentStep)
    }
}