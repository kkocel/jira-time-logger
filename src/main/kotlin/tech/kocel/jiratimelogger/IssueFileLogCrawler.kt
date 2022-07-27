package tech.kocel.jiratimelogger

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IssueFileLogCrawler(
    private val logfileName: String = "commit-log.txt",
    private val finishDayMarkerFileName: String = "finish-day.txt"
) {
    fun readFile(): Sequence<String> {
        val finishDay: LocalDate = readFinishDay()

        val reader = File(logfileName).bufferedReader()

        return reader.lineSequence()
            .filter {
                if (it.contains("[")) {
                    val date = it.dateFromLine().toLocalDate()
                    date > finishDay
                } else {
                    false
                }
            }
    }

    private fun readFinishDay(): LocalDate {
        return LocalDate.parse(File(finishDayMarkerFileName).readText(), DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun saveFinishDay(date: LocalDate) {
        File(finishDayMarkerFileName).writeText(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}
