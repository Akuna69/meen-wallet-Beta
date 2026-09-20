package io.meen.apollo.domain.errors.nfc

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class NoFeasibleZoneForModelError : MuunError() {
    override val classification = ErrorClassification.EXPECTED
}
