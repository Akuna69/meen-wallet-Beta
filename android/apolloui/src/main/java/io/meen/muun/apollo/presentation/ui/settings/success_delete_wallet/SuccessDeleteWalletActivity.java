package io.meen.apollo.presentation.ui.settings.success_delete_wallet;

import io.meen.apollo.R;
import io.meen.apollo.databinding.SuccessDeleteWalletActivityBinding;
import io.meen.apollo.presentation.ui.base.BaseActivity;
import io.meen.apollo.presentation.ui.utils.StyledStringRes;
import io.meen.common.Optional;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import androidx.viewbinding.ViewBinding;
import kotlin.jvm.functions.Function1;

import javax.validation.constraints.NotNull;

public class SuccessDeleteWalletActivity extends BaseActivity<SuccessDeleteWalletPresenter> {

    /**
     * Create an Intent to launch this Activity.
     */
    public static Intent getStartActivityIntent(@NotNull Context context,
                                                Optional<String> supportId) {

        return new Intent(context, SuccessDeleteWalletActivity.class)
                .putExtra(SuccessDeleteWalletView.SUPPORT_ID, supportId.orElse(null));
    }

    private SuccessDeleteWalletActivityBinding binding() {
        return (SuccessDeleteWalletActivityBinding) getBinding();
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    // TODO rm this once all activities have successfully migrated from butterKnife to view binding.
    @Override
    protected int getLayoutResource() {
        return R.layout.success_delete_wallet_activity;
    }

    @Override
    protected Function1<LayoutInflater, ViewBinding> bindingInflater() {
        return SuccessDeleteWalletActivityBinding::inflate;
    }

    @Override
    protected void initializeUi() {
        super.initializeUi();

        setUpDescription();
        binding().successDeleteWalletAction.setOnClickListener(v -> presenter.navigateToLauncher());
    }

    private void setUpDescription() {
        final StyledStringRes styledExplanation = new StyledStringRes(
                getViewContext(),
                R.string.delete_wallet_success_description,
                presenter::navigateToFeedback
        );

        binding().successDeleteWalletDescription.setText(styledExplanation.toCharSequence());
    }
}
