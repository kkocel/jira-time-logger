package tech.kocel.jiratimelogger

interface IssuesPerDayProvider {

    fun provideDaysWithIssues(input: Sequence<String>): List<DayWithIssues>
}
