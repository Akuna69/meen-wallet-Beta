package io.meen.apollo.presentation.ui.settings.edit_username;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.domain.model.user.User;
import io.meen.apollo.presentation.ui.base.BaseView;

interface EditUsernameView extends BaseView {

    void setUsername(User user);

    void setFirstNameError(UserFacingError error);

    void setLastNameError(UserFacingError error);

    void setLoading(boolean loading);
}
