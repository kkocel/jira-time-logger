package tech.kocel.jiratimelogger

import java.io.File
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class RemainingCommitsReader(
    private val clock: Clock,
    private val logfileName: String = "commit-log.txt",
    private val finishDayMarkerFileName: String = "finish-day.txt"
) {
    fun provideRemainingDaysWithIssues(): List<DayWithIssues> = provideDaysWithIssues(readCommitsAfterFinishDay())

    private fun readCommitsAfterFinishDay(): Sequence<String> {
        val finishDay: LocalDate = readFinishDay()

        val reader = File(logfileName).bufferedReader()

        return reader
            .lineSequence()
            .filter {
                if (it.contains("[")) {
                    val date = it.dateFromLine().toLocalDate()
                    date > finishDay
                } else {
                    false
                }
            }
    }

    private fun readFinishDay(): LocalDate =
        try {
            LocalDate.parse(
                File(finishDayMarkerFileName)
                    .apply { createNewFile() }
                    .readText()
                    .trim(),
                DateTimeFormatter.ISO_LOCAL_DATE
            )
        } catch (e: DateTimeParseException) {
            LocalDate.now(clock)
        }

    fun saveFinishDay(date: LocalDate) {
        File(finishDayMarkerFileName).writeText(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    private fun provideDaysWithIssues(input: Sequence<String>): List<DayWithIssues> {
        val issueRegex = Regex("([A-Z]{2,}-\\d+)")

        val duplicateDaysWithIssues: List<DayWithIssues> =
            input
                .mapNotNull { nonNullInput ->
                    if (nonNullInput.contains('[')) {
                        val date = nonNullInput.dateFromLine()
                        val issues =
                            issueRegex
                                .findAll(nonNullInput.substringAfter("]"))
                                .map { matchResult: MatchResult ->
                                    matchResult.groups
                                        .filterNotNull()
                                        .first()
                                        .value
                                }.toList()
                                .distinct()
                        DayWithIssues(
                            date,
                            issues
                        )
                    } else {
                        null
                    }
                }.filter { it.issues.isNotEmpty() }
                .toList()

        val issuesPerDay = duplicateDaysWithIssues.groupBy { it.day.dayOfMonth }

        return issuesPerDay.map { entry ->
            DayWithIssues(
                entry.value.first().day,
                entry.value.flatMap { it.issues }
            )
        }
    }
}
