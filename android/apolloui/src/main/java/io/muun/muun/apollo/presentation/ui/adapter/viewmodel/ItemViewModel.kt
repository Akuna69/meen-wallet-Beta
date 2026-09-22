package io.meen.apollo.presentation.ui.adapter.viewmodel

import io.meen.apollo.presentation.ui.adapter.holder.ViewHolderFactory

interface ItemViewModel {

    fun type(typeFactory: ViewHolderFactory): Int
}
