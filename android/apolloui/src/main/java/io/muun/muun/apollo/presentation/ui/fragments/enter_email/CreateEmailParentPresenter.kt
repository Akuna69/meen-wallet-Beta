package io.meen.apollo.presentation.ui.fragments.enter_email

import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.presentation.ui.base.ParentPresenter
import rx.Observable

interface CreateEmailParentPresenter : ParentPresenter {

    fun refreshToolbarTitle()

    fun submitEmail(email: String)

    fun watchSubmitEmail(): Observable<ActionState<Void>>

    fun cancelCreateEmail()

    fun getEmail(): String?
}