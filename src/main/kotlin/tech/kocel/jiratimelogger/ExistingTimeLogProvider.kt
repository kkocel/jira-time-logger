package tech.kocel.jiratimelogger

import java.time.Duration
import java.time.OffsetDateTime

interface ExistingTimeLogProvider {

    fun howManyHoursLoggedAlready(day: OffsetDateTime): Duration
}
