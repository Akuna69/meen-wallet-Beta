package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.R;
import io.meen.apollo.databinding.MeenDetailItemBinding;
import io.meen.apollo.presentation.ui.utils.UiUtils;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.viewbinding.ViewBinding;
import kotlin.jvm.functions.Function1;

import javax.annotation.Nullable;

public class MeenDetailItem extends MeenView {

    static final ViewProps<MeenDetailItem> viewProps = new ViewProps.Builder<MeenDetailItem>()
            .addStringJava(R.attr.title, MeenDetailItem::setTitle)
            .addStringJava(R.attr.description, MeenDetailItem::setDescription)
            .addInt(android.R.attr.maxLines, MeenDetailItem::setMaxLines)
            .addRefJava(R.attr.icon, MeenDetailItem::setIcon)
            .build();

    private MeenDetailItemBinding binding;

    @Override
    public Function1<View, ViewBinding> viewBinder() {
        return MeenDetailItemBinding::bind;
    }

    private CustomLinkMovementMethod linkMovementMethod;

    public MeenDetailItem(Context context) {
        super(context);
    }

    public MeenDetailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenDetailItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.meen_detail_item;
    }

    @Override
    protected void setUp(Context context, @Nullable AttributeSet attrs) {
        super.setUp(context, attrs);

        linkMovementMethod = new CustomLinkMovementMethod();

        binding = (MeenDetailItemBinding) getBinding();
        viewProps.transfer(attrs, this);
    }

    public void setTitle(CharSequence titleText) {
        binding.operationDetailItemTextTitle.setText(titleText);
    }

    public void setTitleSize(float size) {
        binding.operationDetailItemTextTitle.setTextSize(size);
    }

    /**
     * Sets the icon in the title.
     * @param resId resource id
     */
    public void setTitleIcon(@DrawableRes int resId) {
        final ImageView titleIcon = binding.operationDetailItemIcon;
        if (resId != 0) {
            titleIcon.setImageResource(resId);
            titleIcon.setVisibility(VISIBLE);

        } else {
            titleIcon.setVisibility(GONE);
        }
    }

    public void setTitleIconTint(@ColorRes int colorId) {
        UiUtils.setTint(binding.operationDetailItemIcon, colorId);
    }

    /**
     * Set the long-click listener for this item.
     */
    public void setOnLongClickListener(OnLongClickListener listener) {
        // Android strikes again!
        // There's a number of problems with just setting the long-click listener in this view.

        // First, since children are also clickable, they intercept the event and it never reaches
        // this view. So, instead of adding the listener to the outer frame, we'll add it to each
        // relevant child.

        // Second, links contained in the description text always capture clicks. Just setting the
        // listener will process the long-click, and then *also* trigger the link click. To work
        // around this, we'll wrap the listener and disable links while the long-click is handled.

        final OnLongClickListener wrappedListener = (view) -> {
            linkMovementMethod.ignoreNextTouchEvent = true;
            return listener.onLongClick(view);
        };

        final TextView description = binding.operationDetailItemTextContent;

        description.setLongClickable(true);
        description.setOnLongClickListener(wrappedListener);

        final TextView title = binding.operationDetailItemTextTitle;

        title.setLongClickable(true);
        title.setOnLongClickListener(wrappedListener);
    }

    public void setOnIconClickListener(OnClickListener listener) {
        binding.operationDetailItemIconFrame.setOnClickListener(listener);
    }

    /**
     * Set and show description (if not null/empty).
     */
    public void setDescription(CharSequence descriptionText) {
        final TextView description = binding.operationDetailItemTextContent;

        description.setMovementMethod(linkMovementMethod);
        if (!TextUtils.isEmpty(descriptionText)) {
            description.setText(descriptionText);
            description.setVisibility(VISIBLE);
        }
    }

    public void hideDescription() {
        binding.operationDetailItemTextContent.setVisibility(GONE);
    }

    private void setMaxLines(Integer maxLines) {
        final TextView description = binding.operationDetailItemTextContent;

        description.setMaxLines(maxLines);
        description.setEllipsize(TextUtils.TruncateAt.END);
        description.setMovementMethod(null); // LinkMovementMethod not compat with ellipse/maxLines
    }

    public void setIcon(@DrawableRes int resId) {
        binding.operationDetailItemIcon.setImageResource(resId);
        binding.operationDetailItemIconFrame.setVisibility(VISIBLE);
    }

    /**
     * A LinkMovementMethod for TextViews that allows ignoring touch events.
     */
    private static class CustomLinkMovementMethod extends LinkMovementMethod {
        boolean ignoreNextTouchEvent = false;

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (ignoreNextTouchEvent) {
                ignoreNextTouchEvent = false;
                return false;

            } else {
                return super.onTouchEvent(widget, buffer, event);
            }
        }
    }
}
