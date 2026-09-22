package io.meen.apollo.domain.errors.nfc

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class NoFeasibleZoneForModelError : MeenError() {
    override val classification = ErrorClassification.EXPECTED
}
