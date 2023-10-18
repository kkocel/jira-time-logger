package tech.kocel.jiratimelogger

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class JiraExistingTimeLogProvider(
    baseUrl: String,
    user: String,
    password: String,
    webClientBuilder: WebClient.Builder
) : ExistingTimeLogProvider {
    private val logger = KotlinLogging.logger {}

    private val jiraDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val webclient =
        webClient(
            webClientBuilder = webClientBuilder,
            verboseLogging = false,
            baseUrl = baseUrl,
            connectionProviderName = "jira-existing-time-provider",
            pendingMaxCount = PENDING_ACQUISITION_MAX_COUNT,
            user = user,
            password = password
        )

    override fun howManyHoursLoggedAlready(day: OffsetDateTime): Duration {
        return webclient
            .post()
            .uri("/rest/api/2/search")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                JqlSearch(
                    jql =
                        "worklogAuthor = currentUser() AND worklogDate = ${day.format(jiraDateFormat)} " +
                            "ORDER BY updated ASC",
                    fields = listOf("worklog")
                )
            ).retrieve()
            .bodyToMono<SearchContainer>()
            .map { searchContainer ->
                Duration.ofSeconds(
                    searchContainer.issues.flatMap { result ->
                        result
                            .fields
                            .worklog
                            .worklogs
                            .filter { it.started.toLocalDate() == day.toLocalDate() }
                            .map { it.timeSpentSeconds }
                    }
                        .sum()
                        .toLong()
                )
            }
            .doOnError {
                if (it is WebClientResponseException) {
                    logger.warn { "Can't search for issues at $day body: " + it.responseBodyAsString }
                } else {
                    logger.info { it }
                }
            }
            .onErrorResume(
                WebClientResponseException::class.java
            ) { Mono.just(Duration.ZERO) }
            .block() ?: Duration.ZERO
    }

    companion object {
        const val PENDING_ACQUISITION_MAX_COUNT = 50
    }

    data class SearchContainer(
        val startAt: Int,
        val maxResults: Int,
        val total: Int,
        val issues: List<IssueWorklogSearchResult>
    )

    data class JqlSearch(val jql: String, val fields: List<String>)

    data class IssueWorklogSearchResult(val key: String, val fields: IssueWorklogSearchResultFields)

    data class IssueWorklogSearchResultFields(val worklog: WorklogField)

    data class WorklogField(val worklogs: List<WorklogItem>)

    data class WorklogItem(val timeSpentSeconds: Int, val started: OffsetDateTime)
}
