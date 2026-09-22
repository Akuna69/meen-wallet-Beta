package io.meen.apollo.presentation.ui.fragments.rc_only_login

import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.domain.model.CreateSessionRcOk
import rx.Observable

interface RcOnlyLoginParentPresenter : ParentPresenter {

    fun watchLoginWithRcOnly(): Observable<ActionState<CreateSessionRcOk>>

    fun loginWithRcOnly(recoveryCode: String)

    fun cancelLoginWithRcOnly()
}
