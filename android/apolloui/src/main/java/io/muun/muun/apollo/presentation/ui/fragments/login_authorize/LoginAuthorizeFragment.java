package io.meen.apollo.presentation.ui.fragments.login_authorize;

import io.meen.apollo.R;
import io.meen.apollo.presentation.app.Email;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.fragments.verify_email.VerifyEmailView;
import io.meen.apollo.presentation.ui.utils.StyledStringRes;
import io.meen.apollo.presentation.ui.view.LoadingView;
import io.meen.apollo.presentation.ui.view.MeenButton;
import io.meen.apollo.presentation.ui.view.MeenHeader;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import butterknife.BindView;

public class LoginAuthorizeFragment
        extends SingleFragment<LoginAuthorizePresenter>
        implements VerifyEmailView {

    @BindView(R.id.signup_waiting_for_email_open_email_client)
    MeenButton openEmailAppButton;

    @BindView(R.id.signup_waiting_for_email_verification_title)
    TextView titleView;

    @BindView(R.id.signup_waiting_for_email_verification_explanation)
    TextView descriptionView;

    @BindView(R.id.signup_waiting_for_email_verify_email_icon)
    ImageView emailIcon;

    @BindView(R.id.signup_waiting_for_email_verification_loading)
    LoadingView loadingView;

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.signup_waiting_for_email_verification_fragment;
    }

    @Override
    protected void initializeUi(View view) {
        titleView.setText(R.string.signup_email_authorize);

        openEmailAppButton.setEnabled(Email.INSTANCE.hasEmailAppInstalled(requireContext()));
        openEmailAppButton.setOnClickListener(v -> presenter.openEmailClient());
    }

    @Override
    protected void setUpHeader() {
        final MeenHeader header = getParentActivity().getHeader();
        header.setNavigation(MeenHeader.Navigation.BACK);
        header.setElevated(true);
        header.showTitle(R.string.login_title);
    }

    @Override
    public boolean onBackPressed() {
        presenter.goBack();
        return true;
    }

    @Override
    public void setEmail(@NonNull String email) {

        final StyledStringRes styledDesc = new StyledStringRes(
                requireContext(),
                R.string.signup_email_verify_explanation
        );

        descriptionView.setText(styledDesc.toCharSequence(email));
    }

    @Override
    public void setLoading(boolean isLoading) {
        loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        titleView.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
        descriptionView.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
        emailIcon.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void handleInvalidLinkError() {
        setLoading(false);
        emailIcon.setImageResource(R.drawable.ic_envelope_error);
        titleView.setText(R.string.email_link_error_title);
        descriptionView.setText(R.string.authorize_email_link_invalid);
    }

    @Override
    public void handleExpiredLinkError() {
        setLoading(false);
        emailIcon.setImageResource(R.drawable.ic_envelope_error);
        titleView.setText(R.string.email_link_error_title);
        descriptionView.setText(R.string.authorize_email_link_expired);
    }
}
