package tech.kocel.jiratimelogger

import mu.KotlinLogging
import java.time.Duration

class LoggingOrchestrator(
    private val issueFileLogCrawler: IssueFileLogCrawler,
    private val issuesPerDayProvider: IssuesPerDayProvider,
    private val issueTimePartitioner: EqualIssueTimePartitioner,
    private val workLogger: WorkLogger
) {
    private val logger = KotlinLogging.logger {}

    fun logTimeOnIssues() {
        val daysWithIssues: List<DayWithIssues> =
            issuesPerDayProvider.provideDaysWithIssues(issueFileLogCrawler.readFile())

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
            issueFileLogCrawler.saveFinishDay(date)
            logger.info { "Finished logging at $date" }
        } else {
            logger.info { "No days to log" }
        }
    }
}
