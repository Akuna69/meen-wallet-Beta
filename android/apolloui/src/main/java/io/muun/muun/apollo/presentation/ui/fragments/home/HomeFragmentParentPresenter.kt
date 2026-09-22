package io.meen.apollo.presentation.ui.fragments.home

import io.meen.apollo.presentation.ui.base.ParentPresenter

interface HomeFragmentParentPresenter : ParentPresenter {

    fun navigateToSecurityCenter()

    fun navigateToHighFeesExplanationScreen()

    fun navigateToOperations()
}
