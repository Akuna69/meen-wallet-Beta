package io.meen.apollo.domain.model.auth

import io.meen.common.api.KeySet
import io.meen.common.crypto.ChallengeType

class LoginOk(
    val loginType: ChallengeType, // Should either be PASSWORD or RECOVERY_CODE
    val keySet: KeySet,
)