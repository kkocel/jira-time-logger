package tech.kocel.jiratimelogger

class StringIssuesPerDayProvider : IssuesPerDayProvider {

    override fun provideDaysWithIssues(input: Sequence<String>): List<DayWithIssues> {
        val issueRegex = Regex("([A-Z]{2,}-\\d+)")

        val duplicateDaysWithIssues: List<DayWithIssues> = input.mapNotNull { nonNullInput ->
            if (nonNullInput.contains('[')) {
                val date = nonNullInput.dateFromLine()
                val issues = issueRegex.findAll(nonNullInput.substringAfter("]"))
                    .map { matchResult: MatchResult -> matchResult.groups.filterNotNull().first().value }
                    .toList()
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
