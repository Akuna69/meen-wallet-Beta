package io.meen.apollo.presentation.ui.fragments.phone_number;


import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface PhoneNumberView extends SingleFragmentView {
    void setLoading(boolean isLoading);

    void setPhoneNumberError(UserFacingError error);
}