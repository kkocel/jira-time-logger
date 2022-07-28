package tech.kocel.jiratimelogger

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun String.dateFromLine(): OffsetDateTime = LocalDateTime
    .parse(substringBefore("[").trim(), dateFormat)
    .atOffset(ZoneOffset.UTC)
