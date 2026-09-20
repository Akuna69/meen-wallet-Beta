package io.meen.apollo.domain.errors


import io.meen.common.exception.PotentialBug

class NullExpectedDebtBugError : MuunError(
    "The expectedDebt, in NTS preference, was found to be null"
), PotentialBug {
    override val classification = ErrorClassification.UNEXPECTED
}
