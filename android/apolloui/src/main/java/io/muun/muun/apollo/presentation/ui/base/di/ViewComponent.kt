package io.meen.apollo.presentation.ui.base.di

import dagger.Subcomponent
import io.meen.apollo.presentation.ui.view.FeeManualInput
import io.meen.apollo.presentation.ui.view.MeenAmountInput
import io.meen.apollo.presentation.ui.view.MeenLockOverlay
import io.meen.apollo.presentation.ui.view.MeenNumericKeyboard
import io.meen.apollo.presentation.ui.view.MeenPictureInput
import io.meen.apollo.presentation.ui.view.MeenTextInput

@PerView
@Subcomponent
interface ViewComponent {

    fun inject(meenTextInput: MeenTextInput)

    fun inject(feeManualInput: FeeManualInput)

    fun inject(meenAmountInput: MeenAmountInput)

    fun inject(meenPictureInput: MeenPictureInput)

    fun inject(meenLockOverlay: MeenLockOverlay)

    fun inject(meenNumericKeyboard: MeenNumericKeyboard)
}