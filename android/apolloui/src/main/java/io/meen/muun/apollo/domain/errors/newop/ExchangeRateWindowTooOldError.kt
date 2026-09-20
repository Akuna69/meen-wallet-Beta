package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.exception.PotentialBug

class ExchangeRateWindowTooOldError : MeenError(), PotentialBug {
    override val classification = ErrorClassification.UNEXPECTED
}
