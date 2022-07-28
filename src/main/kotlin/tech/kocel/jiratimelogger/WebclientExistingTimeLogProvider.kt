package tech.kocel.jiratimelogger

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class WebclientExistingTimeLogProvider(
    baseUrl: String,
    user: String,
    password: String,
    webClientBuilder: WebClient.Builder
) : ExistingTimeLogProvider {

    private val jiraDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val webclient =
        webClient(
            webClientBuilder = webClientBuilder,
            verboseLogging = true,
            baseUrl = baseUrl,
            connectionProviderName = "jira-work-logger",
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
                """
                    {
                    "jql": "worklogAuthor = currentUser() 
                    AND worklogDate = ${day.format(jiraDateFormat)} ORDER BY updated ASC",
                    "fields": ["worklog"]
                    }
                """.trimIndent()
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

    data class IssueWorklogSearchResult(val key: String, val fields: IssueWorklogSearchResultFields)

    data class IssueWorklogSearchResultFields(val worklog: WorklogField)

    data class WorklogField(val worklogs: List<WorklogItem>)
    data class WorklogItem(val timeSpentSeconds: Int, val started: OffsetDateTime)
}
