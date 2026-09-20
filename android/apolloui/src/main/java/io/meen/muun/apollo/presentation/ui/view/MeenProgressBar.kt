package io.meen.apollo.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.annotation.ColorRes
import butterknife.BindView
import io.meen.apollo.R
import io.meen.apollo.presentation.ui.utils.UiUtils
import io.meen.apollo.presentation.ui.utils.addOnNextLayoutListener

class MeenProgressBar @JvmOverloads constructor(c: Context, a: AttributeSet? = null, s: Int = 0) :
    MeenView(c, a, s) {

    @BindView(R.id.progress)
    lateinit var progressView: View

    override val layoutResource: Int
        get() = R.layout.meen_progress_bar

    var progress: Double = 0.0
        set(progress) {
            check(progress in 0.0..1.0)
            field = progress

            // Deferring until next layout pass, to be sure views are already measured
            // (and we can make use of our/our parent's width)
            addOnNextLayoutListener {
                adjustBarDimensions()
            }
        }

    var colorRes: Int = 0
        set(@ColorRes color) {
            field = color
            progressView.background = getBackgroundDrawable(colorRes)
        }

    private fun adjustBarDimensions() {
        progressView.layoutParams = LayoutParams((this.width * progress).toInt(), MATCH_PARENT)
    }

    private fun getBackgroundDrawable(@ColorRes colorRes: Int) =
        UiUtils.getTintedDrawable(context, R.drawable.meen_progress_bar_bg, colorRes)
}
