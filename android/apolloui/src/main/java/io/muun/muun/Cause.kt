package io.muun.muun

object Cause {
    val logging: Any = Any()
    val TraceSection: Any = Any()
    
    fun cause(block: () -> Unit) {}
    fun cause(param: Any, block: () -> Unit) {}
    
    fun tostacktracelement(): StackTraceElement {
        return StackTraceElement("", "", "", 0)
    }
}
