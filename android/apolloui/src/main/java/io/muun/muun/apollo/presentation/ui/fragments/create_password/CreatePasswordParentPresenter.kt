package io.meen.apollo.presentation.ui.fragments.create_password

import io.meen.apollo.presentation.ui.base.ParentPresenter

interface CreatePasswordParentPresenter: ParentPresenter {

    fun submitPassword(password: String)

    fun cancelCreatePassword()
}