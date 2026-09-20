package io.meen.apollo.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.MeenSwitchSettingItemBinding

class MeenSwitchSettingItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0,
) : MeenView(context, attrs, style) {

    companion object {
        val viewProps: ViewProps<MeenSwitchSettingItem> = ViewProps.Builder<MeenSwitchSettingItem>()
            .addStringJava(R.attr.label, MeenSwitchSettingItem::setLabel)
            .addBoolean(R.attr.checked, MeenSwitchSettingItem::setChecked)
            .build()
    }

    private val binding: MeenSwitchSettingItemBinding
        get() = getBinding() as MeenSwitchSettingItemBinding

    override val layoutResource: Int
        get() = R.layout.meen_switch_setting_item

    override fun viewBinder(): ((View) -> ViewBinding) {
        return MeenSwitchSettingItemBinding::bind
    }

    override fun setUp(context: Context, attrs: AttributeSet?) {
        super.setUp(context, attrs)
        viewProps.transfer(attrs, this)
    }

    fun setLabel(labelText: CharSequence?) {
        binding.settingItemLabel.text = labelText
    }

    fun setChecked(checked: Boolean) {
        binding.settingItemSwitch.isChecked = checked
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        binding.settingItemSwitch.setOnCheckedChangeListener(listener)
    }
}
