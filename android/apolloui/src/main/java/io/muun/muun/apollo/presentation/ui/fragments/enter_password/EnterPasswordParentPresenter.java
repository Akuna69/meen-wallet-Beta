package io.meen.apollo.presentation.ui.fragments.enter_password;

import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.model.auth.LoginOk;
import io.meen.apollo.presentation.ui.base.ParentPresenter;

import rx.Observable;

public interface EnterPasswordParentPresenter extends ParentPresenter {

    void submitEnterPassword(String password);

    Observable<ActionState<LoginOk>> watchSubmitEnterPassword();

    boolean canUseRecoveryCodeToLogin();

    void useRecoveryCodeToLogin();

    void cancelEnterPassword();
}
