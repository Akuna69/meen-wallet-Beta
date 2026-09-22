package io.meen.apollo.presentation.ui.fragments.login_email

import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.domain.model.SignupDraft
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.domain.model.CreateSessionOk
import rx.Observable

interface LoginEmailParentPresenter: ParentPresenter {

    fun useRecoveryCodeOnlyLogin()

    fun submitEmail(email: String)

    fun watchSubmitEmail(): Observable<ActionState<CreateSessionOk>>

    fun cancelEnterEmail()

    val signupDraft: SignupDraft
}