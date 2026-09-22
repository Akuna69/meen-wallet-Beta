package io.meen.apollo.presentation.ui.recovery_code.success;

import io.meen.apollo.domain.model.user.User;
import io.meen.apollo.presentation.ui.base.BaseView;

interface SuccessRecoveryCodeView extends BaseView {

    void setTexts(User user);
}
