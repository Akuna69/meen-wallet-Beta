package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.UserPreferencesRepository
import io.meen.apollo.domain.model.user.UserPreferences
import rx.Observable
import javax.inject.Inject

class UserPreferencesSelector @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    fun watch(): Observable<UserPreferences> {
        return userPreferencesRepository.watch()
    }

    fun get(): UserPreferences {
        return watch().toBlocking().first()
    }

    fun emailSetupSkipped(): Boolean {
        return get().skippedEmailSetup
    }
}