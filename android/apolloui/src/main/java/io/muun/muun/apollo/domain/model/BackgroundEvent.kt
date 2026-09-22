package io.meen.apollo.domain.model

import kotlinx.serialization.Serializable

@Serializable
class BackgroundEvent(
    val beginTimestampInMillis: Long,
    val durationInMillis: Long,
)