package io.meen.apollo.domain.model

import io.meen.apollo.domain.model.user.User
import io.meen.common.crypto.hd.PublicKey

data class CreateFirstSessionOk(
    val user: User,
    val cosigningPublicKey: PublicKey,
    val swapServerPublicKey: PublicKey,
    val playIntegrityNonce: String?,
)