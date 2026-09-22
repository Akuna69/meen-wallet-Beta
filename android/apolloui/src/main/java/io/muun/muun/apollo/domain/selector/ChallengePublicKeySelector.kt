package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.KeysRepository
import io.meen.common.crypto.ChallengeType
import javax.inject.Inject

open class ChallengePublicKeySelector @Inject constructor(
    private val keysRepository: KeysRepository
) {

    // open so mockito can mock/spy
    open fun exists(type: ChallengeType) =
        keysRepository.hasChallengePublicKey(type)

    fun existsAnyType() =
        exists(ChallengeType.PASSWORD) || exists(ChallengeType.RECOVERY_CODE)
}