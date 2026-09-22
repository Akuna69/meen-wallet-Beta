package io.meen.apollo.presentation.ui.settings;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface RecoveryCodeView extends SingleFragmentView {

    void setRecoveryCodeError(UserFacingError error);

    void setConfirmEnabled(boolean enabled);
}
