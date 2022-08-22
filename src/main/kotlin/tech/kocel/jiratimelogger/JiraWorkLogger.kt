package tech.kocel.jiratimelogger

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
) : WorkLogger {
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

    override fun logWork(day: OffsetDateTime, issue: String, duration: Duration) {
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
                logger.info { "Logged for $day" }
            }
            .doOnError {
                if (it is WebClientResponseException) {
                    logger.warn { "Error for $day and $issue: " + it.responseBodyAsString }
                }
            }
            .onErrorMap { it }
            .block()
    }

    data class DateAndTimeSpent(val timeSpentSeconds: Long, val started: String)

    companion object {
        const val PENDING_ACQUISITION_MAX_COUNT = 50
    }
}
