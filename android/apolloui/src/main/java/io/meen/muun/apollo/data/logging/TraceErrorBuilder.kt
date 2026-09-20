package io.meen.apollo.data.logging

import io.meen.Cause

class TraceErrorBuilder {

    fun build(trace: Trace): Throwable =
        trace.sections.fold(null, this::toError) ?: Throwable()

    private fun toError(prevError: Throwable?, section: TraceSection) =
        Cause(section, prevError)
}