package tech.kocel.jiratimelogger

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun String.dateFromLine(): OffsetDateTime =
    try {
        LocalDateTime
            .parse(substringBefore("[").trim(), dateFormat)
            .atOffset(ZoneOffset.UTC)
    } catch (e: DateTimeParseException) {
        Instant.EPOCH.atOffset(ZoneOffset.UTC)
    }
