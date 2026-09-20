package io.meen.apollo.presentation.biometrics

import androidx.fragment.app.FragmentActivity
import io.meen.apollo.domain.errors.BiometricAuthenticationError

interface BiometricsController {

    /**
     * Returns if user opted for biometrics previously.
     */
    fun hasUserOptedInBiometrics(): Boolean

    /**
     * Sets whether the user opt-in/opt-out for biometrics auth.
     */
    fun setUserOptInBiometrics(optIn: Boolean)

    /**
     * Returns whether the user can authenticate using biometrics (feature flag, device hardware).
     */
    fun getAuthenticationStatus(): BiometricsAuthenticationStatus

    /**
     * Presents biometrics authentication flow.
     */
    fun authenticate(
        activity: FragmentActivity,
        promptTitle: CharSequence,
        promptSubtitle: CharSequence,
        onSuccess: () -> Unit,
        onFailure: (BiometricAuthenticationError) -> Unit,
    )
}
