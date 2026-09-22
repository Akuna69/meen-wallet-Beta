package io.meen.apollo.presentation.ui.signup;

import io.meen.apollo.domain.model.SignupStep;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface SignupView extends SingleFragmentView {

    void changeStep(SignupStep step);
}
