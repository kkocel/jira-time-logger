package tech.kocel.jiratimelogger

import arrow.core.Either
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class JiraWorkLogger(
    baseUrl: String,
    user: String,
    password: String,
    webClientBuilder: WebClient.Builder
) {
    private val logger = KotlinLogging.logger {}

    private val webclient =
        webClient(
            webClientBuilder = webClientBuilder,
            verboseLogging = false,
            baseUrl = baseUrl,
            connectionProviderName = "jira-work-logger",
            pendingMaxCount = PENDING_ACQUISITION_MAX_COUNT,
            user = user,
            password = password
        )

    private val jiraDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    fun logWork(day: OffsetDateTime, issue: String, duration: Duration): Either<Throwable, String?> = Either.catch {
        webclient
            .post()
            .uri("/rest/api/2/issue/{issue}/worklog", issue)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                DateAndTimeSpent(
                    timeSpentSeconds = duration.toSeconds(),
                    started = day.format(jiraDateFormat)
                )
            ).retrieve()
            .bodyToMono<String>()
            .map {
                logger.info { "Logged $duration for $day" }
                it
            }
            .doOnError {
                if (it is WebClientResponseException) {
                    logger.warn { "Error for $day and $issue: " + it.responseBodyAsString }
                } else {
                    logger.warn { "Can't log worklog: $it" }
                }
            }
            .block()
    }

    data class DateAndTimeSpent(val timeSpentSeconds: Long, val started: String)

    companion object {
        const val PENDING_ACQUISITION_MAX_COUNT = 50
    }
}
