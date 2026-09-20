package io.meen.apollo.presentation.ui.fragments.setup_password_success

import android.view.View
import butterknife.BindView
import io.meen.apollo.R
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MeenButton

class SetupPasswordSuccessFragment : SingleFragment<SetupPasswordSuccessPresenter>(), BaseView {

    @BindView(R.id.setup_password_success_action)
    lateinit var actionButton: MeenButton

    override fun inject() =
        component.inject(this)

    override fun getLayoutResource() =
        R.layout.fragment_setup_password_success

    override fun initializeUi(view: View) {
        actionButton.setOnClickListener {
            presenter.finishSetup()
        }
    }

    override fun setUpHeader() {
        parentActivity.header.visibility = View.GONE
    }

    override fun onBackPressed(): Boolean {
        presenter.goBack()
        return true
    }
}