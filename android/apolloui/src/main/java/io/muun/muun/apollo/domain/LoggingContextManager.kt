package io.meen.apollo.domain

import android.content.Context
import io.meen.apollo.data.logging.Crashlytics
import io.meen.apollo.data.logging.LoggingContext
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.domain.utils.locale
import io.meen.common.Optional
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingContextManager @Inject constructor(
    private val userRepository: UserRepository,
    private val context: Context,
) {

    /**
     * Set up Crashlytics metadata.
     */
    fun setupCrashlytics() {
        val maybeUser: Optional<User> = userRepository.fetchOneOptional()

        if (!maybeUser.isPresent) {
            return  // If no LOGGED-IN user do nothing (we handle sign-in flow on its own)
        }

        Crashlytics.configure(maybeUser.get().hid.toString())

        LoggingContext.locale = context.locale().toString()
    }
}