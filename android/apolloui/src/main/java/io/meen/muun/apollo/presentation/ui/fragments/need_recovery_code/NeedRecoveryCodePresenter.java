package io.meen.apollo.presentation.ui.fragments.need_recovery_code;

import io.meen.apollo.presentation.ui.base.BasePresenter;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter;
import io.meen.apollo.presentation.ui.base.di.PerFragment;

import javax.inject.Inject;

@PerFragment
public class NeedRecoveryCodePresenter extends SingleFragmentPresenter<BaseView, BasePresenter> {

    @Inject
    public NeedRecoveryCodePresenter() {
    }

    public void goToRecoveryCodeSetup() {
        navigator.navigateToRecoveryCode(getContext());
        view.finishActivity();
    }
}
