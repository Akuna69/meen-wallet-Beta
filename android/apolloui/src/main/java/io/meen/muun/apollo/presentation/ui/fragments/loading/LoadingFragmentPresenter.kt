package io.meen.apollo.presentation.ui.fragments.loading

import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import javax.inject.Inject

class LoadingFragmentPresenter @Inject constructor():
    SingleFragmentPresenter<BaseView, ParentPresenter>()