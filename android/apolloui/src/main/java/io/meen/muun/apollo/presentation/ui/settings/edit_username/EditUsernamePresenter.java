package io.meen.apollo.presentation.ui.settings.edit_username;

import io.meen.apollo.domain.action.UserActions;
import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.errors.EmptyFieldError;
import io.meen.apollo.domain.model.user.User;
import io.meen.apollo.domain.selector.UserSelector;
import io.meen.apollo.presentation.ui.base.BasePresenter;
import io.meen.apollo.presentation.ui.base.di.PerActivity;

import android.os.Bundle;
import rx.Observable;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@PerActivity
public class EditUsernamePresenter extends BasePresenter<EditUsernameView> {

    private final UserActions userActions;
    private final UserSelector userSel;

    @Inject
    public EditUsernamePresenter(UserActions userActions,
                                 UserSelector userSel) {
        this.userActions = userActions;
        this.userSel = userSel;
    }

    @Override
    public void setUp(@NotNull Bundle arguments) {
        super.setUp(arguments);

        view.setUsername(userSel.get());

        setupUpdateUsernameAction();
    }

    private void setupUpdateUsernameAction() {
        final Observable<ActionState<User>> observable = userActions
                .updateUsernameAction
                .getState()
                .doOnNext(state -> {
                    switch (state.getKind()) {

                        // TODO handle loading state

                        case VALUE:
                            analytics.report(new AnalyticsEvent.E_PROFILE_CHANGED());
                            view.finishActivity();
                            break;

                        case ERROR:
                            view.setLoading(false);
                            handleError(state.getError());
                            break;

                        default:
                            break;
                    }
                });

        subscribeTo(observable);
    }

    /**
     * Validate user name and perform update on Houston.
     */
    public void onSave(String firstName, String lastName) {
        view.setFirstNameError(null);
        view.setLastNameError(null);

        if (firstName.trim().isEmpty()) {
            view.setFirstNameError(new EmptyFieldError(EmptyFieldError.Field.FIRST_NAME));

        } else if (lastName.trim().isEmpty()) {
            view.setLastNameError(new EmptyFieldError(EmptyFieldError.Field.LAST_NAME));

        } else {
            userActions.updateUsernameAction.run(firstName, lastName);
        }
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_EDIT_USERNAME();
    }
}
