package io.meen.apollo.presentation.ui.fragments.create_password

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.FragmentCreatePasswordBinding
import io.meen.apollo.domain.errors.UserFacingError
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MeenButton
import io.meen.apollo.presentation.ui.view.MeenHeader
import io.meen.apollo.presentation.ui.view.MeenTextInput

class CreatePasswordFragment : SingleFragment<CreatePasswordPresenter>(), CreatePasswordView {

    private val binding: FragmentCreatePasswordBinding
        get() = getBinding() as FragmentCreatePasswordBinding

    private val passwordInput: MeenTextInput
        get() = binding.createPasswordInput

    private val passwordConfirmInput: MeenTextInput
        get() = binding.createPasswordConfirmInput

    private val confirmButton: MeenButton
        get() = binding.createPasswordConfirm

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource() =
        R.layout.fragment_create_password

    override fun bindingInflater(): (LayoutInflater, ViewGroup, Boolean) -> ViewBinding {
        return FragmentCreatePasswordBinding::inflate
    }

    override fun initializeUi(view: View) {
        passwordInput.setPasswordRevealEnabled(true)
        passwordInput.setOnChangeListener(this) {
            validatePassword()
        }

        passwordConfirmInput.setPasswordRevealEnabled(true)
        passwordConfirmInput.setOnChangeListener(this) {
            validatePassword()
        }

        confirmButton.isEnabled = false
        confirmButton.setOnClickListener {
            presenter.submitPassword(
                passwordInput.text.toString(),
                passwordConfirmInput.text.toString()
            )
        }
    }

    override fun setUpHeader() {
        // Parent Activity has already taken care of the rest
        parentActivity.header.setNavigation(MeenHeader.Navigation.EXIT)
    }

    override fun onBackPressed(): Boolean {
        presenter.goBack()
        return true
    }

    override fun onResume() {
        super.onResume()
        passwordInput.requestFocusInput()
    }

    override fun setPasswordError(error: UserFacingError?) {
        passwordInput.clearError()

        if (error != null) {
            passwordInput.setError(error)
            passwordInput.requestFocusInput()
        }
    }

    override fun setConfirmPasswordError(error: UserFacingError) {
        passwordConfirmInput.clearError()

        passwordConfirmInput.setError(error)
        passwordConfirmInput.requestFocusInput()
        confirmButton.isEnabled = false
    }

    override fun setLoading(isLoading: Boolean) {
        passwordInput.isEnabled = !isLoading
        confirmButton.setLoading(isLoading)
    }

    private fun validatePassword() {
        val validPassword = presenter.isValidPassword(passwordInput.text.toString())
        val validPasswordConfirm = presenter.isValidPassword(passwordConfirmInput.text.toString())

        confirmButton.isEnabled = validPassword && validPasswordConfirm
    }
}