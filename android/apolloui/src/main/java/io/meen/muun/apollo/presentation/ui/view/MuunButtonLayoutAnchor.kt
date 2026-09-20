package io.meen.apollo.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import io.meen.apollo.R

class MeenButtonLayoutAnchor @JvmOverloads constructor(
    c: Context,
    a: AttributeSet? = null,
    s: Int = 0
) : MeenView(c, a, s) {

    @BindView(R.id.meen_button_layout_anchor)
    lateinit var rootLayout: ViewGroup

    override val layoutResource: Int
        get() = R.layout.meen_button_layout_anchor

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child.id == R.id.meen_button_layout_anchor) {
            return super.addView(child, index, params) // attach our own frame without intervention
        }

        rootLayout.addView(child, params)
    }
}
