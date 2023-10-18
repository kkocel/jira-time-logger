package tech.kocel.jiratimelogger

import java.time.Duration
import java.time.OffsetDateTime

class EqualIssueTimePartitioner(private val existingTimeLogProvider: ExistingTimeLogProvider) {
    fun howLongEachIssueTook(dayWithIssues: DayWithIssues): List<DayWithLoggedIssue> {
        val issues = dayWithIssues.issues.distinct()
        val issueDuration = howLongToLogToday(dayWithIssues.day).dividedBy(issues.size.toLong())

        return issues.map { DayWithLoggedIssue(dayWithIssues.day, it, issueDuration) }
    }

    private fun howLongToLogToday(day: OffsetDateTime): Duration {
        val hoursToLogToday = HOURS_TO_LOG_PER_DAY - existingTimeLogProvider.howManyHoursLoggedAlready(day)

        return if (hoursToLogToday.isNegative) {
            Duration.ZERO
        } else {
            hoursToLogToday
        }
    }

    companion object {
        val HOURS_TO_LOG_PER_DAY: Duration = Duration.ofHours(8)
    }
}
