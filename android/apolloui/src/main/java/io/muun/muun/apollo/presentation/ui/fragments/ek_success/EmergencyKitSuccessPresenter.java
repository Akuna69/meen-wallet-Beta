package io.meen.apollo.presentation.ui.fragments.ek_success;

import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.analytics.AnalyticsEvent.FEEDBACK_TYPE;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.base.di.PerFragment;
import io.meen.apollo.presentation.ui.fragments.single_action.SingleActionPresenter;
import io.meen.apollo.presentation.ui.recovery_code.SetupRecoveryCodePresenter;

import javax.inject.Inject;

@PerFragment
public class EmergencyKitSuccessPresenter
        extends SingleActionPresenter<BaseView, SetupRecoveryCodePresenter> {

    @Inject
    public EmergencyKitSuccessPresenter() {
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_FEEDBACK(FEEDBACK_TYPE.EMERGENCY_KIT_SUCCESS);
    }
}
