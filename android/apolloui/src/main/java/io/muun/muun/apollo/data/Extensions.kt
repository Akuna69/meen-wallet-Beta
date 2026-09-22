package io.meen.apollo.data

import io.meen.apollo.data.serialization.dates.ApolloZonedDateTime
import io.meen.apollo.domain.model.ExchangeRateWindow
import io.meen.common.dates.MeenZonedDateTime
import io.meen.common.model.ExchangeRateProvider
import org.threeten.bp.ZonedDateTime

fun ExchangeRateProvider.getRateWindow(): ExchangeRateWindow =
    ExchangeRateWindow.fromJson(this.rateWindow)

fun MeenZonedDateTime?.toApolloModel(): ZonedDateTime? {
    return if (this == null) {
        null
    } else {
        (this as ApolloZonedDateTime).dateTime
    }
}

fun String.toSafeAscii() =
    this.map { if (it.code > 127) "$UNICODE_PREFIX${it.code.toString(16).padStart(4, '0')}" else it }
        .joinToString("")

private const val UNICODE_PREFIX = "\\u"
