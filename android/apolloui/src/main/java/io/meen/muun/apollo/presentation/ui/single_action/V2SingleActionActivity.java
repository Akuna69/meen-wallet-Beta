package io.meen.apollo.presentation.ui.single_action;

import io.meen.apollo.R;
import io.meen.apollo.databinding.SingleActionActivityV2Binding;
import io.meen.apollo.presentation.ui.base.SingleFragment;
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity;
import io.meen.apollo.presentation.ui.fragments.need_recovery_code.NeedRecoveryCodeFragment;
import io.meen.apollo.presentation.ui.fragments.new_op_error.NewOperationErrorFragment;
import io.meen.apollo.presentation.ui.view.MeenHeader;
import io.meen.common.exception.MissingCaseError;
import io.meen.common.utils.Preconditions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.viewbinding.ViewBinding;
import kotlin.jvm.functions.Function1;

import javax.validation.constraints.NotNull;

public class V2SingleActionActivity extends SingleFragmentActivity<V2SingleActionPresenter> {

    public static final String ACTION_TYPE = "action_type";
    public static final String ACTION_ARGS = "action_args";

    public enum ActionType {
        REQUEST_RECOVERY_CODE_SETUP,
        SHOW_PREPARE_OPERATION_ERROR
    }

    /**
     * Get an Intent to launch an action action fragment, with arguments.
     */
    public static Intent getIntent(@NotNull Context context,
                                   @NotNull ActionType actionType,
                                   @NotNull Bundle arguments) {

        return new Intent(context, V2SingleActionActivity.class)
                .putExtra(ACTION_TYPE, actionType.name())
                .putExtra(ACTION_ARGS, arguments);
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.single_action_activity_v2;
    }

    @Override
    protected Function1<LayoutInflater, ViewBinding> bindingInflater() {
        return SingleActionActivityV2Binding::inflate;
    }

    @Override
    protected int getFragmentsContainer() {
        return R.id.fragment_container;
    }

    @Override
    public MeenHeader getHeader() {
        // this activity has no header/toolbar. Should it?
        // TODO add a toolbar (and hide it) or handle it better
        return null;
    }

    @Override
    protected SingleFragment getInitialFragment() {
        final SingleFragment fragment = createActionFragment();
        final Bundle arguments = getArgumentsBundle().getBundle(ACTION_ARGS);

        Preconditions.checkState(arguments != null);

        fragment.setArguments(arguments);
        return fragment;
    }

    private SingleFragment createActionFragment() {
        final String actionTypeName = getArgumentsBundle().getString(ACTION_TYPE);

        Preconditions.checkState(actionTypeName != null);

        final ActionType type = ActionType.valueOf(actionTypeName);

        switch (type) {
            case REQUEST_RECOVERY_CODE_SETUP:
                return new NeedRecoveryCodeFragment();

            case SHOW_PREPARE_OPERATION_ERROR:
                return new NewOperationErrorFragment();

            default:
                throw new MissingCaseError(type);
        }
    }
}
