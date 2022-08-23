package tech.kocel.jiratimelogger

import mu.KotlinLogging
import java.time.Clock
import java.time.Duration
import java.time.LocalDate

class LoggingOrchestrator(
    private val remainingCommitsReader: RemainingCommitsReader,
    private val issueTimePartitioner: EqualIssueTimePartitioner,
    private val workLogger: JiraWorkLogger,
    private val clock: Clock
) {
    private val logger = KotlinLogging.logger {}

    fun logTimeOnIssues() {
        val daysWithIssues: List<DayWithIssues> = remainingCommitsReader.provideRemainingDaysWithIssues()

        daysWithIssues.map(issueTimePartitioner::howLongEachIssueTook)
            .forEach { dayWithLoggedIssues ->
                dayWithLoggedIssues
                    .filter {
                        if (it.loggedTime > Duration.ZERO) {
                            true
                        } else {
                            logger.info { "Not logging work ${it.day.toLocalDate()} - already logged" }
                            false
                        }
                    }
                    .forEach { workLogger.logWork(it.day, it.issue, it.loggedTime) }
            }

        if (daysWithIssues.isNotEmpty()) {
            val date = daysWithIssues.last().day.toLocalDate()
            remainingCommitsReader.saveFinishDay(date)
            logger.info { "Finished logging at $date" }
        } else {
            remainingCommitsReader.saveFinishDay(LocalDate.now(clock))
            logger.info { "No days to log" }
        }
    }
}
