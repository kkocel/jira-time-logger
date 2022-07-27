package tech.kocel.jiratimelogger

import mu.KotlinLogging
import java.time.Duration
import java.time.OffsetDateTime

class FakeWorkLogger : WorkLogger {
    private val logger = KotlinLogging.logger {}

    override fun logWork(day: OffsetDateTime, issue: String, duration: Duration) {
        logger.info { "$day Logging $duration for issue $issue" }
    }
}
