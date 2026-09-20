package io.meen.apollo.presentation.ui.fragments.enter_recovery_code;

import io.meen.apollo.domain.analytics.AnalyticsEvent;
import io.meen.apollo.domain.errors.IncorrectRecoveryCodeError;
import io.meen.apollo.domain.errors.InvalidChallengeSignatureError;
import io.meen.apollo.domain.errors.rc.InvalidCharacterRecoveryCodeError;
import io.meen.apollo.domain.libwallet.errors.InvalidRecoveryCodeFormatError;
import io.meen.apollo.domain.model.RecoveryCode;
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter;
import io.meen.apollo.presentation.ui.base.di.PerFragment;
import io.meen.apollo.presentation.ui.settings.RecoveryCodeView;

import android.os.Bundle;
import rx.Observable;

import javax.inject.Inject;

import static io.meen.apollo.domain.analytics.AnalyticsEvent.S_INPUT_RECOVERY_CODE;

@PerFragment
public class EnterRecoveryCodePresenter
        extends SingleFragmentPresenter<RecoveryCodeView, EnterRecoveryCodeParentPresenter> {

    /**
     * Creates a presenter.
     */
    @Inject
    public EnterRecoveryCodePresenter() {
    }

    @Override
    public void setUp(Bundle arguments) {
        super.setUp(arguments);

        setUpSubmitRecoveryCode();
    }

    private void setUpSubmitRecoveryCode() {
        final Observable<?> observable = getParentPresenter()
                .watchSubmitEnterRecoveryCode()
                .compose(handleStates(view::setLoading, this::handleError));

        subscribeTo(observable);
    }

    @Override
    public void handleError(Throwable error) {
        if (error instanceof InvalidChallengeSignatureError) {
            view.setRecoveryCodeError(new IncorrectRecoveryCodeError());

        } else {
            super.handleError(error);
        }
    }

    /**
     * Dismiss this presenter's fragment and go back to the previous one in the flow.
     */
    public void goBack() {
        getParentPresenter().cancelEnterRecoveryCode();
    }

    /**
     * Called when the user edits some part of the recovery code.
     */
    public void onRecoveryCodeEdited(String recoveryCodeString) {
        view.setRecoveryCodeError(null);
        view.setConfirmEnabled(false);

        try {
            RecoveryCode.validate(recoveryCodeString);
            view.setConfirmEnabled(true);

        } catch (RecoveryCode.RecoveryCodeAlphabetError | InvalidRecoveryCodeFormatError error) {
            view.setRecoveryCodeError(new InvalidCharacterRecoveryCodeError());

        } catch (RecoveryCode.RecoveryCodeLengthError error) {
            // Do nothing. Let the user finish typing.

        } catch (Exception error) {
            handleError(error);
        }
    }

    /**
     * Sign Houston's challenge with this recovery code and attempt to fetch keys.
     */
    public void submitRecoveryCode(String recoveryCode) {
        getParentPresenter().submitEnterRecoveryCode(recoveryCode);
    }

    @Override
    protected AnalyticsEvent getEntryEvent() {
        return new S_INPUT_RECOVERY_CODE();
    }
}
