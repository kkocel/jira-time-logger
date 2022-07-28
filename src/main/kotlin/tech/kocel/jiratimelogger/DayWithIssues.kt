package tech.kocel.jiratimelogger

import java.time.OffsetDateTime

data class DayWithIssues(
    val day: OffsetDateTime,
    val issues: List<String>
)
