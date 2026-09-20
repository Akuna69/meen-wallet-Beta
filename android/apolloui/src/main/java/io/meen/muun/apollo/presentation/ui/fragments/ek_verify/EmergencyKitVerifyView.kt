package io.meen.apollo.presentation.ui.fragments.ek_verify

import io.meen.apollo.domain.errors.UserFacingError
import io.meen.apollo.presentation.ui.base.BaseView

interface EmergencyKitVerifyView: BaseView {
    fun setVerificationError(error: UserFacingError)

    fun setLoading(isLoading: Boolean)
}