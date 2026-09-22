package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import newop.State

class NewOpStateError(actual: Class<out State>?, expected: Class<out State>) :
    MeenError("Unexpected state. Actual: ${actual?.simpleName} Expected: ${expected.simpleName}") {
    override val classification = ErrorClassification.UNEXPECTED
}