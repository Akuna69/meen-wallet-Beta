package io.meen.apollo.domain.errors

class PeriodicTaskError(taskName: String, duration: Long, cause: Throwable) : MeenError(cause) {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["task"] = taskName
        metadata["duration(secs)"] = duration
        metadata["causeMessage"] = cause.message ?: "null"
        metadata["cause"] = cause.javaClass.toString()
        metadata["causeStackTrace"] = cause.stackTraceToString()
    }
}