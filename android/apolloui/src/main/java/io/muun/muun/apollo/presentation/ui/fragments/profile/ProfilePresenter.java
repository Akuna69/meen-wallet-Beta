package io.meen.apollo.presentation.ui.fragments.profile;

import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.analytics.AnalyticsEvent.S_MORE_INFO_TYPE;
import io.meen.apollo.domain.errors.EmptyFieldError;
import io.meen.apollo.domain.model.user.UserProfile;
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter;
import io.meen.apollo.presentation.ui.base.di.PerFragment;
import io.meen.common.Optional;

import android.net.Uri;
import android.os.Bundle;
import rx.Observable;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;


@PerFragment
public class ProfilePresenter extends SingleFragmentPresenter<ProfileView, ProfileParentPresenter> {

    /**
     * Constructor.
     */
    @Inject
    public ProfilePresenter() {
    }

    @Override
    public void setUp(@NotNull Bundle arguments) {
        super.setUp(arguments);

        setUpSubmitProfileAction();
    }

    private void setUpSubmitProfileAction() {
        final Observable<ActionState<UserProfile>> observable = getParentPresenter()
                .watchSubmitProfile()
                .doOnNext(state -> {
                    view.setLoading(state.isLoading());

                    if (state.isError()) {
                        handleError(state.getError());
                    }
                });

        subscribeTo(observable);
    }

    /**
     * Use the manual profile to signup.
     */
    public void submitManualProfile(String firstName,
                                    String lastName,
                                    Optional<Uri> pictureUri) {
        view.setFirstNameError(null);
        view.setLastNameError(null);

        if (firstName.trim().isEmpty()) {
            view.setFirstNameError(new EmptyFieldError(EmptyFieldError.Field.FIRST_NAME));

        } else if (lastName.trim().isEmpty()) {
            view.setLastNameError(new EmptyFieldError(EmptyFieldError.Field.LAST_NAME));

        } else {
            getParentPresenter().submitProfile(firstName, lastName, pictureUri);
        }
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_P2P_SETUP_PROFILE();
    }

    /**
     * Report show profile info analytics event.
     */
    public void reportShowProfileInfo() {
        analytics.report(new AnalyticsEvent.S_MORE_INFO(S_MORE_INFO_TYPE.PROFILE));
    }
}
