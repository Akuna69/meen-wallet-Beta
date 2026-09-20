package io.meen.apollo.presentation.ui.settings;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

interface OldPasswordView extends SingleFragmentView {

    void setPasswordError(UserFacingError error);
}
