package io.meen.apollo.presentation.ui.view;


import io.meen.apollo.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

import javax.annotation.Nullable;

public class MeenIconButton extends MeenView {

    static final ViewProps<MeenIconButton> viewProps = new ViewProps.Builder<MeenIconButton>()
            .addRefJava(R.attr.icon, MeenIconButton::setIcon)
            .addSizeJava(R.attr.iconSize, MeenIconButton::setIconSize)
            .addColorList(R.attr.color, MeenIconButton::setColor)
            .addStringJava(R.attr.label, MeenIconButton::setLabel)
            .addSizeJava(R.attr.labelSize, MeenIconButton::setLabelSize)
            .build();

    @BindView(R.id.meen_icon_button_container)
    ViewGroup container;

    @BindView(R.id.meen_icon_button_image)
    ImageView icon;

    @BindView(R.id.meen_icon_button_text)
    TextView label;

    ColorStateList colorList;

    public MeenIconButton(Context context) {
        super(context);
    }

    public MeenIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.meen_icon_button;
    }

    @Override
    protected void setUp(Context context, @Nullable AttributeSet attrs) {
        super.setUp(context, attrs);

        setClickable(true);
        setFocusable(true);
        setColor(ContextCompat.getColorStateList(getContext(), R.color.blue));

        viewProps.transfer(attrs, this);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateColor();
    }

    public void setIcon(@DrawableRes int resId) {
        icon.setImageResource(resId);
    }

    public void setIconSize(int pixelSize) {
        icon.setLayoutParams(new LinearLayout.LayoutParams(pixelSize, pixelSize));
        icon.requestLayout();
    }

    public void setColor(ColorStateList colorList) {
        this.colorList = colorList;
        updateColor();
    }

    public void setLabel(CharSequence content) {
        label.setText(content);
        setLabelVisible(label.length() > 0);
    }

    /**
     * Set button's text size, in px.
     */
    public void setLabelSize(int pixelSize) {
        setLabelSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
    }

    /**
     * Set text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     */
    public void setLabelSize(int unit, float size) {
        label.setTextSize(unit, size);
    }

    public void setLabelVisible(boolean isVisible) {
        label.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void updateColor() {
        final int stateColor = colorList.getColorForState(getDrawableState(), Color.TRANSPARENT);

        icon.setColorFilter(stateColor);
        label.setTextColor(stateColor);
    }
}
