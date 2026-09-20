package io.meen.apollo.presentation.ui.setup_p2p;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity;
import io.meen.apollo.presentation.ui.view.MeenHeader;

import android.content.Context;
import android.content.Intent;
import butterknife.BindView;

import javax.validation.constraints.NotNull;

public class SetupP2PActivity extends SingleFragmentActivity<SetupP2PPresenter>
        implements BaseView {

    /**
     * Creates an intent to launch this activity.
     */
    public static Intent getStartActivityIntent(@NotNull Context context) {
        return new Intent(context, SetupP2PActivity.class);
    }

    @BindView(R.id.setup_p2p_header)
    MeenHeader header;

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.setup_p2p_activity;
    }

    @Override
    protected int getFragmentsContainer() {
        return R.id.setup_p2p_frame_container;
    }

    @Override
    public MeenHeader getHeader() {
        return header;
    }

    @Override
    protected SingleFragment getInitialFragment() {
        return presenter.getInitialStep();
    }
}
