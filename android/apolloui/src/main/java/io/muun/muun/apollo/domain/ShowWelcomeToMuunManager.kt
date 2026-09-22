package io.meen.apollo.domain

import io.meen.apollo.data.preferences.UserRepository
import javax.inject.Inject

class ShowWelcomeToMeenManager @Inject constructor(
    private val userRepository: UserRepository,
) {

    fun getSeen(): Boolean =
        userRepository.welcomeToMeenDialogSeen

    fun setSeen() {
        userRepository.setWelcomeToMeenDialogSeen()
    }
}