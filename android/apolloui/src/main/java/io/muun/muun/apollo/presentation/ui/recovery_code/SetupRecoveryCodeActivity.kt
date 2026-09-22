package io.meen.apollo.presentation.ui.recovery_code

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.RecoveryCodeActivityBinding
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.activity.extension.MeenDialog
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity
import io.meen.apollo.presentation.ui.fragments.error.ErrorFragmentDelegate
import io.meen.apollo.presentation.ui.fragments.error.ErrorViewModel
import io.meen.apollo.presentation.ui.recovery_code.priming.PrimingRecoveryCodeFragment
import io.meen.apollo.presentation.ui.view.MeenHeader

internal class SetupRecoveryCodeActivity : SingleFragmentActivity<SetupRecoveryCodePresenter>(),
    SetupRecoveryCodeView,
    ErrorFragmentDelegate {

    companion object {
        const val SET_UP_RC_STEP_COUNT = 3

        /**
         * Creates an intent to launch this activity.
         */
        @JvmStatic
        fun getStartActivityIntent(context: Context): Intent {
            return Intent(context, SetupRecoveryCodeActivity::class.java)
        }
    }

    private val binding: RecoveryCodeActivityBinding
        get() = getBinding() as RecoveryCodeActivityBinding

    private val meenHeader: MeenHeader
        get() = binding.recoveryCodeHeader

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource(): Int =
        R.layout.recovery_code_activity

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return RecoveryCodeActivityBinding::inflate
    }

    override fun getFragmentsContainer(): Int =
        R.id.fragment_container

    override fun getHeader(): MeenHeader =
        meenHeader

    override fun getInitialFragment() =
        PrimingRecoveryCodeFragment()

    override fun initializeUi() {
        super.initializeUi()
        meenHeader.attachToActivity(this)
        meenHeader.setNavigation(MeenHeader.Navigation.EXIT)
    }

    override fun setUser(user: User) {
        if (user.hasPassword) {
            meenHeader.showTitle(R.string.security_center_title_improve_your_security)
        } else {
            meenHeader.showTitle(R.string.security_center_title_backup_your_wallet)
        }
    }

    override fun showAbortDialog() {
        val meenDialog = MeenDialog.Builder()
            .title(R.string.recovery_code_abort_title)
            .message(R.string.recovery_code_abort_body)
            .positiveButton(R.string.abort) { presenter.onSetupAborted() }
            .negativeButton(R.string.cancel)
            .build()
        showDialog(meenDialog)
    }

    override fun handleStartRecoveryCodeSetupConnectionError() {
        showError(
            ErrorViewModel.Builder()
                .loggingName(AnalyticsEvent.ERROR_TYPE.RC_SETUP_START_CONNECTION_ERROR)
                .kind(ErrorViewModel.ErrorViewKind.RETRYABLE)
                .title(getString(R.string.rc_setup_start_connection_error_title))
                .descriptionRes(R.string.rc_setup_start_connection_error_desc)
                .canGoBack(true)
                .build()
        )
    }

    override fun handleFinishRecoveryCodeSetupConnectionError() {
        showError(
            ErrorViewModel.Builder()
                .loggingName(AnalyticsEvent.ERROR_TYPE.RC_SETUP_FINISH_CONNECTION_ERROR)
                .kind(ErrorViewModel.ErrorViewKind.RETRYABLE)
                .title(getString(R.string.rc_setup_finish_connection_error_title))
                .descriptionRes(R.string.rc_setup_finish_connection_error_desc)
                .canGoBack(true)
                .build()
        )
    }

    override fun handleRetry(errorType: AnalyticsEvent.ERROR_TYPE) {
        hideError()
        if (errorType == AnalyticsEvent.ERROR_TYPE.RC_SETUP_START_CONNECTION_ERROR) {
            presenter.retryRecoveryCodeSetupStart()

        } else if (errorType == AnalyticsEvent.ERROR_TYPE.RC_SETUP_FINISH_CONNECTION_ERROR) {
            presenter.retryRecoveryCodeSetupFinish()
        }
    }

    override fun handleBack(errorType: AnalyticsEvent.ERROR_TYPE) {
        hideError()
        if (errorType == AnalyticsEvent.ERROR_TYPE.RC_SETUP_START_CONNECTION_ERROR) {
            presenter.handleBackFromErrorInStart()

        } else if (errorType == AnalyticsEvent.ERROR_TYPE.RC_SETUP_FINISH_CONNECTION_ERROR) {
            presenter.handleBackFromErrorInFinish()
        }
    }
}