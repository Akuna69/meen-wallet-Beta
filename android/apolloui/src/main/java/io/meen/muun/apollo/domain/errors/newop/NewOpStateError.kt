package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError
import newop.State

class NewOpStateError(actual: Class<out State>?, expected: Class<out State>) :
    MuunError("Unexpected state. Actual: ${actual?.simpleName} Expected: ${expected.simpleName}") {
    override val classification = ErrorClassification.UNEXPECTED
}