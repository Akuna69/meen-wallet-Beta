package io.meen.apollo.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import butterknife.BindView
import io.meen.apollo.R

class MeenSaveOption @JvmOverloads constructor(c: Context, a: AttributeSet? = null, s: Int = 0) :
    MeenView(c, a, s) {

    companion object {
        val viewProps: ViewProps<MeenSaveOption> = ViewProps.Builder<MeenSaveOption>()
            .addRef(R.attr.icon) { v: MeenSaveOption, resId: Int -> v.icon = resId }
            .addString(R.attr.title) { v: MeenSaveOption, str: String -> v.title = str }
            .addString(R.attr.description) { v: MeenSaveOption, str: String ->
                v.description = str
            }
            .build()
    }

    @BindView(R.id.icon)
    lateinit var iconView: ImageView

    @BindView(R.id.title)
    lateinit var titleView: TextView

    @BindView(R.id.description)
    lateinit var descriptionView: TextView

    @BindView(R.id.label)
    lateinit var labelView: MeenSaveOptionLabel

    override val layoutResource: Int
        get() = R.layout.meen_save_option

    override fun setUp(context: Context, attrs: AttributeSet?) {
        super.setUp(context, attrs)

        viewProps.transfer(attrs, this)
    }

    var title: CharSequence = ""
        set(value) {
            titleView.text = value
            field = value
        }

    var description: CharSequence = ""
        set(value) {
            descriptionView.text = value
            field = value
        }

    @DrawableRes
    var icon: Int = 0
        set(value) {
            iconView.setImageResource(value)
            field = value
        }

    var labelKind: MeenSaveOptionLabel.Kind? = null
        set(value) {
            if (value != null) {
                labelView.visibility = View.VISIBLE
                labelView.kind = value
            } else {
                labelView.visibility = View.GONE
            }

            field = value
        }
}