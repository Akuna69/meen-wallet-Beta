package io.meen.apollo.presentation.ui.fragments.phone_number;

import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.model.user.UserPhoneNumber;
import io.meen.apollo.presentation.ui.base.ParentPresenter;
import io.meen.common.model.PhoneNumber;

import rx.Observable;

public interface PhoneNumberParentPresenter extends ParentPresenter {

    /**
     * Submit a phone number as part of the P2P setup.
     */
    void submitPhoneNumber(PhoneNumber phoneNumber);

    /**
     * Get an Observable for the SubmitPhoneNumber async action.
     */
    Observable<ActionState<UserPhoneNumber>> watchSubmitPhoneNumber();

}
