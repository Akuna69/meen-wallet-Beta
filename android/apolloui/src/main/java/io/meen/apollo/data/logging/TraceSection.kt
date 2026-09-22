package io.muun.apollo.data.logging

open class TraceCause(
    open val className: String = "",
    open val message: String? = null
)

open class TraceLine {
    open fun toStackTraceElement(): StackTraceElement =
        StackTraceElement("", "", "", 0)
}

open class TraceSection(
    open val cause: TraceCause = TraceCause(),
    open val lines: List<TraceLine> = emptyList()
)
