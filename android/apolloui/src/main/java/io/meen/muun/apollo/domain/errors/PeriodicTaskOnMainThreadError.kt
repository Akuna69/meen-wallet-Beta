package io.meen.apollo.domain.errors

class PeriodicTaskOnMainThreadError(taskName: String) : MeenError() {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["task"] = taskName
    }
}