package io.meen.apollo.presentation.ui.recovery_tool;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity;
import io.meen.apollo.presentation.ui.fragments.recovery_tool.RecoveryToolFragment;
import io.meen.apollo.presentation.ui.fragments.recovery_tool.RecoveryToolPresenter;
import io.meen.apollo.presentation.ui.view.MeenHeader;
import io.meen.apollo.presentation.ui.view.MeenHeader.Navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import butterknife.BindView;

import javax.validation.constraints.NotNull;

public class RecoveryToolActivity extends SingleFragmentActivity<RecoveryToolActivityPresenter>
        implements BaseView {

    @BindView(R.id.header)
    MeenHeader header;

    /**
     * Creates an intent to launch this activity.
     */
    public static Intent getStartActivityIntent(@NotNull Context context) {
        return new Intent(context, RecoveryToolActivity.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_recovery_tool;
    }

    @Override
    protected int getFragmentsContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected void initializeUi() {
        super.initializeUi();

        header.attachToActivity(this);
        header.setBackgroundColor(Color.TRANSPARENT);
        header.showTitle(R.string.recovery_tool_header);
        header.setNavigation(Navigation.BACK);
    }

    @Override
    protected SingleFragment<RecoveryToolPresenter> getInitialFragment() {
        return new RecoveryToolFragment();
    }

    @Override
    public MeenHeader getHeader() {
        return header;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }
}
