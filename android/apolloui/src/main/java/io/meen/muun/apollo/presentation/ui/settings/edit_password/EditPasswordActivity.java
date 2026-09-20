package io.meen.apollo.presentation.ui.settings.edit_password;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity;
import io.meen.apollo.presentation.ui.view.MeenHeader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import butterknife.BindView;

import javax.validation.constraints.NotNull;

public class EditPasswordActivity extends SingleFragmentActivity<EditPasswordPresenter>
        implements EditPasswordView {

    /**
     * Creates an intent to launch this activity.
     */
    public static Intent getStartActivityIntent(@NotNull Context context) {
        return new Intent(context, EditPasswordActivity.class);
    }

    @BindView(R.id.edit_password_header)
    MeenHeader header;

    @Override protected void inject() {
        getComponent().inject(this);
    }

    @Override protected int getLayoutResource() {
        return R.layout.edit_password_activity;
    }

    @Override
    protected void initializeUi() {
        super.initializeUi();

        header.attachToActivity(this);
        header.setBackgroundColor(Color.TRANSPARENT);
        header.setNavigation(MeenHeader.Navigation.BACK);
        header.showTitle(R.string.settings_password);
        header.setElevated(true);
    }

    @Override
    protected int getFragmentsContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected SingleFragment getInitialFragment() {
        return new StartPasswordChangeFragment();
    }

    public MeenHeader getHeader() {
        return header;
    }
}
