package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.AuthRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.common.model.SessionStatus
import rx.Observable
import javax.inject.Inject


class LoginAuthorizedSelector @Inject constructor(
    val authRepository: AuthRepository,
    val userRepository: UserRepository
) {

    fun watch(targetStatus: SessionStatus): Observable<Boolean> =
        authRepository.watchSessionStatus()
            .map { it.orElse(null) }
            .map { it == targetStatus }
}