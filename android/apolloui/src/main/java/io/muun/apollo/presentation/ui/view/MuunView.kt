package io.muun.apollo.presentation.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import butterknife.ButterKnife
import io.muun.apollo.domain.errors.BugDetected
import io.muun.apollo.presentation.ui.activity.extension.ExternalResultExtension
import io.muun.apollo.presentation.ui.activity.extension.PermissionManagerExtension
import io.muun.apollo.presentation.ui.base.BaseActivity
import io.muun.apollo.presentation.ui.base.di.ViewComponent
import io.muun.apollo.presentation.ui.utils.BundleSizeLogger
import io.muun.apollo.presentation.ui.utils.locale
import timber.log.Timber
import java.util.LinkedList
import java.util.Locale

abstract class MuunView : FrameLayout,
    ExternalResultExtension.Caller,
    PermissionManagerExtension.PermissionRequester {

    private companion object {
        private const val PARENT_STATE = "parentState"
        private const val OWN_STATE = "ownState"
        private const val CHILD_STATE = "childState"
    }

    private var isInflated = false

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val layoutRes = layoutResource
        if (layoutRes != 0) {
            LayoutInflater.from(context).inflate(layoutRes, this, true)
        }
        isInflated = true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInflated) {
            onViewCreated(this)
        }
    }

    @get:LayoutRes
    protected open val layoutResource: Int
        get() = 0

    protected open fun onViewCreated(view: View) {
        ButterKnife.bind(this, view)
        setUpComponent(component)
    }

    protected open fun setUpComponent(component: ViewComponent) { me.inject(component) }

    protected open val me: MuunView
        get() = this

    protected open fun inject(component: ViewComponent) {
        // Option to override in subclasses
    }

    protected val component: ViewComponent
        get() = (context as BaseActivity<*>).component.viewComponent()

    protected val parentActivity: BaseActivity<*>
        get() {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is BaseActivity<*>) {
                    return ctx
                }
                ctx = ctx.baseContext
            }
            throw BugDetected("MuunView attached to non-BaseActivity context")
        }

    override fun onSaveInstanceState(): Parcelable {
        val state = Bundle()
        val parentState = super.onSaveInstanceState()
        val ownState = Bundle()
        val childState = SparseArray<Parcelable>()

        for (i in 0 until childCount) {
            getChildAt(i).saveHierarchyState(childState)
        }

        state.putParcelable(PARENT_STATE, parentState)
        state.putParcelable(OWN_STATE, ownState)
        state.putSparseParcelableArray(CHILD_STATE, childState)

        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val parentState = state.getParcelable<Parcelable>(PARENT_STATE)
            val childState = state.getSparseParcelableArray<Parcelable>(CHILD_STATE)

            if (childState != null) {
                for (i in 0 until childCount) {
                    getChildAt(i).restoreHierarchyState(childState)
                }
            }

            super.onRestoreInstanceState(parentState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        parentActivity.requestPermissions(this, permissions, requestCode)
    }

    override fun onPermissionsGranted(requestCode: Int) {}

    override fun onPermissionsDenied(requestCode: Int) {}

    override fun startExternalActivityForResult(intent: Intent, requestCode: Int) {
        parentActivity.startExternalActivityForResult(this, intent, requestCode)
    }

    override fun onExternalActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
}
