package io.muun.apollo.presentation.ui.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.FrameLayout

abstract class MuunView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val OWN_STATE = "ownState"
        private const val CHILD_STATE = "childState"
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = Bundle()
        val ownState = super.onSaveInstanceState()
        val childState = SparseArray<Parcelable>()
        for (i in 0 until childCount) {
            getChildAt(i).saveHierarchyState(childState)
        }
        state.putParcelable(OWN_STATE, ownState)
        state.putSparseParcelableArray(CHILD_STATE, childState)
        return state
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        if (parcelable is Bundle) {
            val ownState = parcelable.getParcelable<Parcelable>(OWN_STATE)
            val childState = parcelable.getSparseParcelableArray<Parcelable>(CHILD_STATE)
            for (i in 0 until childCount) {
                getChildAt(i).restoreHierarchyState(childState)
            }
            super.onRestoreInstanceState(ownState)
        } else {
            super.onRestoreInstanceState(parcelable)
        }
    }
}
