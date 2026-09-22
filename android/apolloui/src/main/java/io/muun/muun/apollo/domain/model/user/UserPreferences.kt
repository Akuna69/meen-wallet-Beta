package io.meen.apollo.domain.model.user

import io.meen.common.model.ReceiveFormatPreference

data class UserPreferences(
    val strictMode: Boolean,
    val seenNewHome: Boolean,
    val seenLnurlFirstTime: Boolean,
    val defaultAddressType: String,
    val skippedEmailSetup: Boolean,
    val receivePreference: ReceiveFormatPreference,
    val allowMultiSession: Boolean,
) {
    fun toJson(): io.meen.common.model.UserPreferences {
        return io.meen.common.model.UserPreferences(
            strictMode,
            seenNewHome,
            seenLnurlFirstTime,
            defaultAddressType,
            false,
            skippedEmailSetup,
            receivePreference,
            allowMultiSession
        )
    }

    companion object {

        @JvmStatic
        fun fromJson(prefs: io.meen.common.model.UserPreferences): UserPreferences {
            return UserPreferences(
                prefs.receiveStrictMode,
                prefs.seenNewHome,
                prefs.seenLnurlFirstTime,
                prefs.defaultAddressType,
                prefs.skippedEmailSetup,
                prefs.receiveFormatPreference,
                prefs.allowMultiSession
            )
        }
    }
}