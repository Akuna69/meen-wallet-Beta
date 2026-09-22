package io.meen.apollo.presentation.ui.fragments.enter_recovery_code;

import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.model.auth.LoginOk;
import io.meen.apollo.presentation.ui.base.ParentPresenter;

import rx.Observable;

public interface EnterRecoveryCodeParentPresenter extends ParentPresenter {

    void submitEnterRecoveryCode(String recoveryCode);

    Observable<ActionState<LoginOk>> watchSubmitEnterRecoveryCode();

    void cancelEnterRecoveryCode();
}
