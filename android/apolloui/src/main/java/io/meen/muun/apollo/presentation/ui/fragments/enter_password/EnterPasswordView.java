package io.meen.apollo.presentation.ui.fragments.enter_password;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;


public interface EnterPasswordView extends SingleFragmentView {
    void setLoading(boolean isLoading);

    void setReminderVisible(boolean isVisible);

    void setPasswordError(UserFacingError error);

    void setForgotPasswordVisible(boolean isVisible);
}
