package io.meen.apollo.domain.model

import io.meen.apollo.domain.model.feebump.FeeBumpFunctions

class FulfillmentPushedResult(
    val nextTransactionSize: NextTransactionSize,
    val feeBumpFunctions: FeeBumpFunctions
)