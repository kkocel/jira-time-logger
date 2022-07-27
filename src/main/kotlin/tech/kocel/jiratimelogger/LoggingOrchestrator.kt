package tech.kocel.jiratimelogger

import mu.KotlinLogging

class LoggingOrchestrator(
    private val issueFileLogCrawler: IssueFileLogCrawler,
    private val issuesPerDayProvider: IssuesPerDayProvider,
    private val issueTimePartitioner: EqualIssueTimePartitioner,
    private val workLogger: WorkLogger
) {
    private val logger = KotlinLogging.logger {}

    fun logTimeOnIssues() {
        val daysWithIssues: List<DayWithIssues> = issuesPerDayProvider.provideDaysWithIssues(issueFileLogCrawler.readFile())

        daysWithIssues.map(issueTimePartitioner::howLongEachIssueTook)
            .forEach { dayWithLoggedIssues ->
                dayWithLoggedIssues.forEach { workLogger.logWork(it.day, it.issue, it.loggedTime) }
            }

        if (daysWithIssues.isNotEmpty()) {
            issueFileLogCrawler.saveFinishDay(daysWithIssues.last().day.toLocalDate())
        } else {
            logger.info { "No days to log" }
        }
    }
}
