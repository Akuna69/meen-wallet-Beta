package io.meen.apollo.presentation.ui.fragments.verify_email

import io.meen.apollo.presentation.ui.base.ParentPresenter

interface VerifyEmailParentPresenter: ParentPresenter {

    fun getEmail(): String?

    fun cancelVerifyEmail()

}