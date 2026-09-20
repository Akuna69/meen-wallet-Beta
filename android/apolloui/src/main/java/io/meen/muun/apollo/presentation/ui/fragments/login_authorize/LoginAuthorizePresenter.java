package io.meen.apollo.presentation.ui.fragments.login_authorize;

import io.meen.apollo.data.external.Globals;
import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.action.session.UseMeenLinkAction;
import io.meen.apollo.domain.action.user.EmailLinkAction;
import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.errors.ExpiredActionLinkError;
import io.meen.apollo.domain.errors.InvalidActionLinkError;
import io.meen.apollo.domain.selector.LoginAuthorizedSelector;
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter;
import io.meen.apollo.presentation.ui.base.di.PerFragment;
import io.meen.apollo.presentation.ui.fragments.verify_email.VerifyEmailView;
import io.meen.apollo.presentation.ui.utils.UiNotificationPoller;
import io.meen.common.model.SessionStatus;

import android.os.Bundle;
import androidx.annotation.Nullable;
import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

@PerFragment
public class LoginAuthorizePresenter extends
        SingleFragmentPresenter<VerifyEmailView, LoginAuthorizeParentPresenter> {

    private final UiNotificationPoller notificationPoller;
    private final LoginAuthorizedSelector loginAuthorizedSel;
    private final UseMeenLinkAction useMeenLinkAction;
    private final EmailLinkAction emailLinkAction;

    /**
     * Creates a presenter.
     */
    @Inject
    public LoginAuthorizePresenter(UiNotificationPoller notificationPoller,
                                   LoginAuthorizedSelector loginAuthorizedSel,
                                   UseMeenLinkAction useMeenLinkAction,
                                   EmailLinkAction emailLinkAction) {

        this.notificationPoller = notificationPoller;
        this.loginAuthorizedSel = loginAuthorizedSel;
        this.useMeenLinkAction = useMeenLinkAction;
        this.emailLinkAction = emailLinkAction;
    }

    @Override
    public void setUp(Bundle arguments) {
        super.setUp(arguments);

        emailLinkAction.setPending(Globals.INSTANCE.getAuthorizeLinkPath());

        watchForEmailLinkErrors();
        watchForAuthorizeSignin();
        notificationPoller.start();

        view.setEmail(getParentPresenter().getSignupDraft().getEmail());
    }

    @Override
    public void tearDown() {
        super.tearDown();
        notificationPoller.stop();
    }

    private void watchForEmailLinkErrors() {
        final Observable<?> observable = useMeenLinkAction
                .getState()
                .compose(handleStates(view::setLoading, this::handleError))
                .doOnNext(ignored -> {
                    // This is a hackish attempt to handle Houston's ActionLinkAlreadyUsedException
                    // which is currently returned as a 200 OK, empty response (like a successful
                    // request). "Why?", you ask. GO FIGURE (seems like retrocompat blablity).
                    // Bear in mind that if we are in a success scenario, we will probably continue
                    // to next step (due to notification poller) before timer/loading finishes.
                    final Observable<Long> timerObservable = Observable.timer(3, TimeUnit.SECONDS)
                            .compose(getAsyncExecutor())
                            .doOnNext(someValue -> view.handleInvalidLinkError());

                    subscribeTo(timerObservable);
                });

        subscribeTo(observable);
    }

    private void watchForAuthorizeSignin() {
        final Observable<?> observable = loginAuthorizedSel.watch(SessionStatus.AUTHORIZED_BY_EMAIL)
                .filter(isAuthorized -> isAuthorized)
                .doOnNext(it -> getParentPresenter().reportEmailVerified());

        subscribeTo(observable);
    }

    void openEmailClient() {
        navigator.navigateToEmailClient(getContext());
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new AnalyticsEvent.S_AUTHORIZE_EMAIL();
    }

    void goBack() {
        getParentPresenter().cancelEmailVerification();
    }

    @Override
    public void handleError(Throwable error) {
        if (error instanceof InvalidActionLinkError) {
            view.handleInvalidLinkError();

        } else if (error instanceof ExpiredActionLinkError) {
            view.handleExpiredLinkError();

        } else {
            super.handleError(error);
        }
    }

    /**
     * Custom ActionState handling to allow loading until an external caller decides it.
     */
    protected <T> Observable.Transformer<ActionState<T>, T> handleStates(
            @Nullable Action1<Boolean> handleLoading,
            @Nullable Action1<Throwable> handleError) {

        return observable -> observable
                .doOnNext(state -> {
                    if (handleLoading != null && state.isLoading()) {
                        handleLoading.call(true);
                    }

                    if (handleError != null && state.isError()) {
                        handleError.call(state.getError());
                    }
                })
                .filter(ActionState::isValue)
                .map(ActionState::getValue);
    }
}
