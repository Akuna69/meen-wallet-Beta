package io.meen.apollo.presentation.ui.recovery_code.priming

import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.base.SingleFragmentView

interface PrimingRecoveryCodeView : SingleFragmentView {

    /**
     * Set this view's texts based on the current state of the user.
     */
    fun setTexts(user: User)

    /**
     * Set whether the start Recovery Code setup request is taking place or not.
     */
    fun handleLoading(isLoading: Boolean)

}