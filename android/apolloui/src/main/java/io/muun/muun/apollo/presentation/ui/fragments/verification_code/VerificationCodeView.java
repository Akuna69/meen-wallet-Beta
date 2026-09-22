package io.meen.apollo.presentation.ui.fragments.verification_code;


import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface VerificationCodeView extends SingleFragmentView {

    /**
     * Set this view's model.
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Set this view's loading state.
     */
    void setLoading(boolean isLoading);

    /**
     * Handle an error with the submitted verification code.
     */
    void handleVerificationCodeError(Throwable error);
}
