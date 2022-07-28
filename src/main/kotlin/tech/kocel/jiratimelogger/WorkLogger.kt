package tech.kocel.jiratimelogger

import java.time.Duration
import java.time.OffsetDateTime

interface WorkLogger {
    fun logWork(day: OffsetDateTime, issue: String, duration: Duration)
}
