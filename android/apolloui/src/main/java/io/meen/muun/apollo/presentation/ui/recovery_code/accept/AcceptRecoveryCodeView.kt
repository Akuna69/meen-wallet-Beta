package io.meen.apollo.presentation.ui.recovery_code.accept

import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.base.SingleFragmentView

interface AcceptRecoveryCodeView : SingleFragmentView {

    /**
     * Set this view's texts based on the current state of the user.
     */
    fun setTexts(user: User)

    /**
     * Set this view's state to loading.
     */
    override fun setLoading(isLoading: Boolean)
}