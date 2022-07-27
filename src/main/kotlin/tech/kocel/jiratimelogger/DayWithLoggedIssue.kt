package tech.kocel.jiratimelogger

import java.time.Duration
import java.time.OffsetDateTime

data class DayWithLoggedIssue(
    val day: OffsetDateTime,
    val issue: String,
    val loggedTime: Duration
)
