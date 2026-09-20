package io.meen.apollo.presentation.ui.settings.edit_password;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface ChangePasswordView extends SingleFragmentView {

    void setPasswordError(UserFacingError error);

    void setConfirmPasswordError(UserFacingError error);

}
