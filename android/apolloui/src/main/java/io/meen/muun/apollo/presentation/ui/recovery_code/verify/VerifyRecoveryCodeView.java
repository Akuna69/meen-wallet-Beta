package io.meen.apollo.presentation.ui.recovery_code.verify;


import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.domain.libwallet.RecoveryCodeV2;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

import java.util.List;

public interface VerifyRecoveryCodeView extends SingleFragmentView {

    void setRecoveryCode(RecoveryCodeV2 recoveryCode);

    void setSegmentsToVerify(List<Integer> segmentsToVerify);

    void setVerificationError(UserFacingError error);

    void setConfirmEnabled(boolean isEnabled);
}
