package io.meen.apollo.domain.action.session

import io.meen.apollo.data.preferences.AuthRepository
import io.meen.apollo.data.preferences.BiometricsRepository
import io.meen.apollo.domain.action.LogoutActions
import io.meen.apollo.domain.action.UserActions
import io.meen.apollo.domain.errors.UnclassifiedError
import io.meen.common.Optional
import timber.log.Timber
import javax.inject.Inject

class LogoutAction @Inject constructor(
    private val logoutActions: LogoutActions,
    private val userActions: UserActions,
    private val authRepository: AuthRepository,
    private val biometricsRepository: BiometricsRepository,
) {

    fun run() {
        val jwt: String = getJwt()
        logoutActions.destroyRecoverableWallet()
        userActions.notifyLogoutAction.run(jwt)
        biometricsRepository.deleteUserOptInBiometrics()
    }

    private fun getJwt(): String {
        val serverJwt: Optional<String> = authRepository.serverJwt
        if (!serverJwt.isPresent) {
            // Shouldn't happen but we wanna know 'cause probably a bug
            Timber.e(UnclassifiedError("Auth token expected to be present"))
            return ""
        }
        return serverJwt.get()
    }
}