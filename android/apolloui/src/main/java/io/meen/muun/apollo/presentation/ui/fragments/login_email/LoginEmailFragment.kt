package io.meen.apollo.presentation.ui.fragments.login_email

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.LoginEmailBinding
import io.meen.apollo.domain.errors.UserFacingError
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MuunHeader
import io.meen.common.bitcoinj.ValidationHelpers

class LoginEmailFragment : SingleFragment<LoginEmailPresenter>(), LoginEmailView {

    private val binding: LoginEmailBinding
        get() = getBinding() as LoginEmailBinding

    override fun bindingInflater(): (LayoutInflater, ViewGroup, Boolean) -> ViewBinding {
        return LoginEmailBinding::inflate
    }

    override fun inject() =
        component.inject(this)

    override fun getLayoutResource() =
        R.layout.login_email

    override fun initializeUi(view: View) {
        with(binding) {
            enterEmailInput.apply {
                setOnChangeListener(this@LoginEmailFragment) { validateEmailInput() }
                requestFocusInput()
            }
            validateEmailInput()

            enterEmailAction.setOnClickListener {
                presenter.submitEmail(enterEmailInput.text.toString())
            }

            enterEmailUseRcOnly.setOnClickListener {
                presenter.useRecoveryCodeOnlyLogin()
            }
        }
    }

    override fun setUpHeader() {
        parentActivity.header.apply {
            setNavigation(MuunHeader.Navigation.BACK)
            showTitle(R.string.login_title)
            setElevated(true)
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.goBack()
        return true
    }

    override fun setLoading(isLoading: Boolean) {
        binding.enterEmailAction.setLoading(isLoading)
    }

    override fun autoFillEmail(email: String) {
        binding.enterEmailInput.setText(email)
    }

    override fun setEmailError(error: UserFacingError?) {
        binding.enterEmailInput.setError(error)
    }

    private fun validateEmailInput() {
        setEmailError(null)
        binding.enterEmailAction.isEnabled = ValidationHelpers.isValidEmail(binding.enterEmailInput.text.toString())
    }
}