package tech.kocel.jiratimelogger

import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

class InMemoryExistingTimeLogProvider : ExistingTimeLogProvider {
    private val loggedByDay: MutableMap<LocalDate, Duration> = mutableMapOf()

    override fun howManyHoursLoggedAlready(day: OffsetDateTime): Duration =
        loggedByDay[day.toLocalDate()] ?: Duration.ZERO

    fun addLog(day: OffsetDateTime, duration: Duration) {
        loggedByDay[day.toLocalDate()] = howManyHoursLoggedAlready(day) + duration
    }
}
