package tech.kocel.jiratimelogger

interface IssueTimePartitioner {
    fun howLongEachIssueTook(dayWithIssues: DayWithIssues): List<DayWithLoggedIssue>
}
