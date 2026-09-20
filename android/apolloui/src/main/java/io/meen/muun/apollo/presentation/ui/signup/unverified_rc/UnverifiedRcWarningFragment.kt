package io.meen.apollo.presentation.ui.signup.unverified_rc

import android.view.View
import butterknife.BindView
import io.meen.apollo.R
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MeenButton

class UnverifiedRcWarningFragment : SingleFragment<UnverifiedRcWarningPresenter>() {

    @BindView(R.id.rc_unverified_warning_button)
    lateinit var continueButton: MeenButton

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource(): Int =
        R.layout.fragment_rc_unverified_warning

    override fun initializeUi(view: View) {
        continueButton.setOnClickListener {
            presenter.proceedToHome()
        }
    }

    override fun setUpHeader() {
        parentActivity.header.visibility = View.GONE
    }
}