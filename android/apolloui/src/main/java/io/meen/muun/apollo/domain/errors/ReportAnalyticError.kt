package io.meen.apollo.domain.errors

class ReportAnalyticError(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}