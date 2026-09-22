package io.meen.apollo.presentation.ui.feedback.anon;

import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.analytics.AnalyticsEvent.S_SUPPORT_TYPE;
import io.meen.apollo.domain.selector.UserSelector;
import io.meen.apollo.presentation.ui.base.BasePresenter;
import io.meen.apollo.presentation.ui.base.di.PerActivity;
import io.meen.common.Optional;

import android.os.Bundle;
import androidx.annotation.Nullable;
import icepick.State;

import javax.inject.Inject;

@PerActivity
public class AnonFeedbackPresenter extends BasePresenter<AnonFeedbackView> {

    @State
    @Nullable
    String supportId;

    /**
     * Constructor.
     */
    @Inject
    public AnonFeedbackPresenter(UserSelector userSel) {
        this.userSel = userSel;
    }

    @Override
    public void setUp(Bundle arguments) {
        super.setUp(arguments);
        supportId = arguments.getString(AnonFeedbackView.SUPPORT_ID, null);

        view.setSupportId(Optional.ofNullable(supportId));
    }

    @Nullable
    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_SUPPORT(S_SUPPORT_TYPE.ANON_SUPPORT);
    }

    /**
     * Open Email client, already at the compose/send email (to our support team) screen.
     */
    void openEmailClient() {
        navigator.sendSupportEmail(getContext());
    }
}
