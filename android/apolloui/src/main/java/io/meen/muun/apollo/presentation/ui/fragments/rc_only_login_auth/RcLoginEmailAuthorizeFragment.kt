package io.meen.apollo.presentation.ui.fragments.rc_only_login_auth

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import io.meen.apollo.R
import io.meen.apollo.presentation.app.Email
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.utils.StyledStringRes
import io.meen.apollo.presentation.ui.view.HtmlTextView
import io.meen.apollo.presentation.ui.view.LoadingView
import io.meen.apollo.presentation.ui.view.MeenButton
import io.meen.apollo.presentation.ui.view.MeenHeader

class RcLoginEmailAuthorizeFragment : SingleFragment<RcLoginEmailAuthorizePresenter>(),
    RcLoginEmailAuthorizeView {

    @BindView(R.id.open_email_client)
    lateinit var openEmailAppButton: MeenButton

    @BindView(R.id.rc_login_email_auth_title)
    lateinit var titleView: TextView

    @BindView(R.id.rc_login_email_auth_description)
    lateinit var descriptionView: HtmlTextView

    @BindView(R.id.rc_login_email_auth_icon)
    lateinit var emailIcon: ImageView

    @BindView(R.id.rc_login_email_auth_loading)
    lateinit var loadingView: LoadingView

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource(): Int =
        R.layout.fragment_rc_login_email_auth

    override fun initializeUi(view: View) {
        titleView.setText(R.string.signup_email_authorize)
        openEmailAppButton.isEnabled = Email.hasEmailAppInstalled(requireContext())
        openEmailAppButton.setOnClickListener { presenter.openEmailClient() }
    }

    override fun setUpHeader() {
        val header = parentActivity.header
        header.setNavigation(MeenHeader.Navigation.BACK)
        header.setElevated(true)
        header.showTitle(R.string.login_title)
    }

    override fun onBackPressed(): Boolean {
        presenter.goBack()
        return true
    }

    override fun setObfuscatedEmail(obfuscatedEmail: String) {
        val styledDesc = StyledStringRes(
            requireContext(),
            R.string.signup_email_verify_explanation
        )

        descriptionView.text = styledDesc.toCharSequence(obfuscatedEmail)
    }

    override fun setLoading(isLoading: Boolean) {
        loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        titleView.visibility = if (!isLoading) View.VISIBLE else View.GONE
        descriptionView.visibility = if (!isLoading) View.VISIBLE else View.GONE
        emailIcon.visibility = if (!isLoading) View.VISIBLE else View.GONE
    }

    override fun handleInvalidLinkError() {
        setLoading(false)
        emailIcon.setImageResource(R.drawable.ic_envelope_error)
        titleView.setText(R.string.email_link_error_title)
        descriptionView.setText(R.string.authorize_email_link_invalid)
    }

    override fun handleExpiredLinkError() {
        setLoading(false)
        emailIcon.setImageResource(R.drawable.ic_envelope_error)
        titleView.setText(R.string.email_link_error_title)
        descriptionView.setText(R.string.authorize_email_link_expired)
    }
}