package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.R;
import io.meen.common.utils.Preconditions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import butterknife.BindView;
import icepick.State;

import java.util.HashMap;

public class MeenActionDrawer extends MeenView {

    public interface OnActionClickListener {
        void onActionClick(int actionId);
    }

    @Nullable   // Subclasses may not have this view
    @BindView(R.id.meen_action_drawer_container)
    ViewGroup container;

    @Nullable   // Subclasses may not have this view
    @BindView(R.id.meen_action_drawer_title)
    TextView title;

    @State
    HashMap<Integer, Integer> actionIdToViewIndex = new HashMap<>();

    private OnActionClickListener listener;

    public MeenActionDrawer(Context context) {
        super(context);
    }

    public MeenActionDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenActionDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.meen_action_drawer;
    }

    public void setOnItemClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    public void setTitle(@StringRes int resId) {
        title.setText(resId);
        title.setVisibility(VISIBLE);
    }

    public void setTitle(CharSequence text) {
        title.setText(text);
        title.setVisibility(VISIBLE);
    }

    /**
     * Add an action item to this drawer.
     */
    public void addAction(int actionId,
                          @Nullable Drawable maybeIcon,
                          @Nullable @DrawableRes Integer maybeIconRes,
                          String label) {

        Preconditions.checkNotNull(container);

        final MeenActionDrawerItem item = new MeenActionDrawerItem(getContext());

        if (maybeIcon != null) {
            item.setIcon(maybeIcon);

        } else if (maybeIconRes != null) {
            item.setIcon(maybeIconRes);
        }

        item.setLabel(label);
        item.setOnClickListener(v -> reportItemClick(actionId));

        actionIdToViewIndex.put(actionId, container.getChildCount());
        container.addView(item);
    }

    public void setActionLabel(int actionId, String text) {
        final MeenActionDrawerItem item = getItem(actionId);
        if (item != null) {
            item.setLabel(text);
        }
    }

    public void setActionEnabled(int actionId, boolean enabled) {
        final MeenActionDrawerItem item = getItem(actionId);
        if (item != null) {
            item.setEnabled(enabled);
        }
    }

    private MeenActionDrawerItem getItem(int actionId) {
        Preconditions.checkNotNull(container);

        final Integer index = actionIdToViewIndex.get(actionId);
        if (index != null) {
            final View child = container.getChildAt(index);

            Preconditions.checkState(child instanceof MeenActionDrawerItem);

            return (MeenActionDrawerItem) child;
        }
        return null;
    }

    private void reportItemClick(int actionId) {
        if (listener != null) {
            listener.onActionClick(actionId);
        }
    }
}
