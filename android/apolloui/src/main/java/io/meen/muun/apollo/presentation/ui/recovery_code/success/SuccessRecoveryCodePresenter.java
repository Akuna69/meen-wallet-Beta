package io.meen.apollo.presentation.ui.recovery_code.success;

import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.presentation.ui.base.di.PerFragment;
import io.meen.apollo.presentation.ui.fragments.single_action.SingleActionPresenter;
import io.meen.apollo.presentation.ui.recovery_code.SetupRecoveryCodePresenter;

import android.os.Bundle;

import javax.inject.Inject;

@PerFragment
public class SuccessRecoveryCodePresenter
        extends SingleActionPresenter<SuccessRecoveryCodeView, SetupRecoveryCodePresenter> {

    @Inject
    public SuccessRecoveryCodePresenter() {
    }

    @Override
    public void setUp(Bundle arguments) {
        super.setUp(arguments);

        view.setTexts(userSel.get());
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_FEEDBACK(AnalyticsEvent.FEEDBACK_TYPE.RECOVERY_CODE_SUCCESS);
    }
}
