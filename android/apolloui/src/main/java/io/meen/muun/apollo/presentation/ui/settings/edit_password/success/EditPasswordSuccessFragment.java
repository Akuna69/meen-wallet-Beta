package io.meen.apollo.presentation.ui.settings.edit_password.success;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.fragments.single_action.SingleActionFragment;
import io.meen.apollo.presentation.ui.fragments.single_action.SingleActionPresenter;
import io.meen.apollo.presentation.ui.settings.edit_password.EditPasswordPresenter;

import android.view.View;

public class EditPasswordSuccessFragment
        extends SingleActionFragment<SingleActionPresenter<BaseView, EditPasswordPresenter>> {

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getImageRes() {
        return R.drawable.tick;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.change_password_success_message);
    }

    @Override
    protected int getActionLabelRes() {
        return R.string.change_password_success_action;
    }

    @Override
    protected void setUpHeader() {
        getParentActivity().getHeader().setVisibility(View.GONE);
    }

    @Override
    public boolean onBackPressed() {
        finishActivity();
        return true;
    }
}
