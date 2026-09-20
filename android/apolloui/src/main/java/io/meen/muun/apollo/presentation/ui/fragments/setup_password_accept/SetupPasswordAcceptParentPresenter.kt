package io.meen.apollo.presentation.ui.fragments.setup_password_accept

import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.presentation.ui.base.ParentPresenter
import rx.Observable

interface SetupPasswordAcceptParentPresenter: ParentPresenter {

    fun acceptPasswordSetupTerms()

    fun watchAcceptPasswordSetupTerms(): Observable<ActionState<Void>>

    fun cancelAcceptTerms()

}