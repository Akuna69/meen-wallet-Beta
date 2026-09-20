package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.R;
import io.meen.apollo.databinding.MeenSettingItemBinding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.viewbinding.ViewBinding;
import kotlin.jvm.functions.Function1;

import javax.annotation.Nullable;

public class MeenSettingItem extends MeenView {

    static final ViewProps<MeenSettingItem> viewProps = new ViewProps.Builder<MeenSettingItem>()
            .addStringJava(R.attr.label, MeenSettingItem::setLabel)
            .addStringJava(R.attr.description, MeenSettingItem::setDescription)
            .addRefJava(R.attr.icon, MeenSettingItem::setIcon)
            .addColorList(R.attr.iconTint, MeenSettingItem::setIconColor)
            .build();

    private MeenSettingItemBinding binding;

    @Override
    public Function1<View, ViewBinding> viewBinder() {
        return MeenSettingItemBinding::bind;
    }

    public MeenSettingItem(Context context) {
        super(context);
    }

    public MeenSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected int getLayoutResource() {
        return R.layout.meen_setting_item;
    }

    @Override
    protected void setUp(Context context, @Nullable AttributeSet attrs) {
        super.setUp(context, attrs);

        binding = (MeenSettingItemBinding) getBinding();
        viewProps.transfer(attrs, this);
    }

    public void setLabel(CharSequence labelText) {
        binding.settingItemLabel.setText(labelText);
    }

    public void setDescription(@StringRes int descriptionRes) {
        showDescription(getContext().getString(descriptionRes));
        binding.settingItemDescriptionIcon.setVisibility(GONE);
    }

    public void setDescription(CharSequence descriptionText) {
        showDescription(descriptionText);
        binding.settingItemDescriptionIcon.setVisibility(GONE);
    }

    public void setDescription(CharSequence descriptionText, @DrawableRes int descriptionIconRes) {
        showDescription(descriptionText);
        setDescriptionIcon(descriptionIconRes);
    }

    private void showDescription(CharSequence descriptionText) {
        final TextView description = binding.settingItemDescription;
        description.setText(descriptionText);
        description.setVisibility(View.VISIBLE);
    }

    private void setDescriptionIcon(@DrawableRes int descriptionIconRes) {
        final ImageView descriptionIcon = binding.settingItemDescriptionIcon;
        descriptionIcon.setImageResource(descriptionIconRes);
        descriptionIcon.setVisibility(VISIBLE);
    }

    public void setIcon(@DrawableRes int resId) {
        setIcon(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setIcon(Drawable drawable) {
        final ImageView icon = binding.settingItemIcon;
        icon.setImageDrawable(drawable);
        icon.setVisibility(drawable != null ? View.VISIBLE : View.GONE);
    }

    public void setIconColor(ColorStateList color) {
        ImageViewCompat.setImageTintList(binding.settingItemIcon, color);
    }
}
