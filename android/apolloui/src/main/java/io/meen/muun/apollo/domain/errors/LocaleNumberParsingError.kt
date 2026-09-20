package io.meen.apollo.domain.errors

import java.util.*

class LocaleNumberParsingError(number: String, locale: Locale, cause: Throwable) : MeenError(
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["numberString"] = number
        metadata["locale"] = locale.toString()
    }
}