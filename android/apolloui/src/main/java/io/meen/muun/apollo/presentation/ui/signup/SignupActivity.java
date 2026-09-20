package io.meen.apollo.presentation.ui.signup;

import io.meen.apollo.R;
import io.meen.apollo.domain.model.SignupStep;
import io.meen.apollo.presentation.ui.activity.extension.ApplicationLockExtension;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity;
import io.meen.apollo.presentation.ui.fragments.enter_password.EnterPasswordFragment;
import io.meen.apollo.presentation.ui.fragments.enter_recovery_code.EnterRecoveryCodeFragment;
import io.meen.apollo.presentation.ui.fragments.landing.LandingFragment;
import io.meen.apollo.presentation.ui.fragments.landing.LandingPresenter;
import io.meen.apollo.presentation.ui.fragments.login_authorize.LoginAuthorizeFragment;
import io.meen.apollo.presentation.ui.fragments.login_email.LoginEmailFragment;
import io.meen.apollo.presentation.ui.fragments.rc_only_login.RcOnlyLoginFragment;
import io.meen.apollo.presentation.ui.fragments.rc_only_login_auth.RcLoginEmailAuthorizeFragment;
import io.meen.apollo.presentation.ui.fragments.sync.SyncFragment;
import io.meen.apollo.presentation.ui.signup.unverified_rc.UnverifiedRcWarningFragment;
import io.meen.apollo.presentation.ui.view.MuunHeader;
import io.meen.apollo.presentation.ui.view.MuunHeader.Navigation;
import io.meen.common.exception.MissingCaseError;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

import javax.validation.constraints.NotNull;

public class SignupActivity extends SingleFragmentActivity<SignupPresenter>
        implements SignupView {

    /**
     * Creates an intent to launch this activity.
     */
    public static Intent getStartActivityIntent(@NotNull Context context) {
        return new Intent(context, SignupActivity.class);
    }

    @BindView(R.id.signup_header)
    MuunHeader header;

    @Override
    protected void setUpExtensions() {
        super.setUpExtensions();
        getExtension(ApplicationLockExtension.class).setRequireUnlock(false);
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void initializeUi() {
        header.attachToActivity(this);
        header.setBackgroundColor(Color.TRANSPARENT);
        header.setNavigation(Navigation.NONE); // let each fragment decide
        header.hideTitle();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.signup_activity;
    }

    @Override
    protected int getFragmentsContainer() {
        return R.id.signup_frame_container;
    }

    @Override
    protected SingleFragment<LandingPresenter> getInitialFragment() {
        return LandingFragment.newInstanceWithAnimation();
    }

    @Override
    public void changeStep(SignupStep step) {
        replaceFragment(createStepFragment(step), false);
    }

    public MuunHeader getHeader() {
        return header;
    }

    private Fragment createStepFragment(SignupStep step) {
        switch (step) {
            case START:
                return LandingFragment.newInstance();

            case LOGIN_EMAIL:
                return new LoginEmailFragment();

            case LOGIN_RECOVERY_CODE_ONLY:
                return new RcOnlyLoginFragment();

            case LOGIN_RECOVERY_CODE_EMAIL_AUTH:
                return new RcLoginEmailAuthorizeFragment();

            case LOGIN_WAIT_VERIFICATION:
                return new LoginAuthorizeFragment();

            case LOGIN_PASSWORD:
                return new EnterPasswordFragment();

            case SYNC:
                return new SyncFragment();

            case UNVERIFIED_RC_WARNING:
                return new UnverifiedRcWarningFragment();

            case LOGIN_RECOVERY_CODE:
                return new EnterRecoveryCodeFragment();

            default:
                throw new MissingCaseError(step);
        }
    }
}
