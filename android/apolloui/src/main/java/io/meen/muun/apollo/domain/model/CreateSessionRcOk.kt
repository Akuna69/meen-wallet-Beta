package io.meen.apollo.domain.model

import io.meen.common.api.KeySet

data class CreateSessionRcOk(
    val keySet: KeySet?,        // Null if hasEmailSetup = true
    @JvmField val hasEmailSetup: Boolean,
    val obfuscatedEmail: String?,    // Null if hasEmailSetup = false
    val playIntegrityNonce: String?,
)