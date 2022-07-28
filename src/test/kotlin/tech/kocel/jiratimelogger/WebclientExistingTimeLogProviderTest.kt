package tech.kocel.jiratimelogger

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest
@Suppress("LongMethod")
class WebclientExistingTimeLogProviderTest
(@Autowired private val webclientBuilder: WebClient.Builder) {

    @Test
    fun `should return 8h duration for one worklog`() {
        val wireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wireMockServer.start()
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/rest/api/2/search"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
{
    "expand": "names,schema",
    "startAt": 0,
    "maxResults": 50,
    "total": 1,
    "issues":
    [
        {
            "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
            "id": "1818171",
            "self": "https://jira.com/rest/api/2/issue/1818171",
            "key": "IPA-1606",
            "fields":
            {
                "worklog":
                {
                    "startAt": 0,
                    "maxResults": 20,
                    "total": 1,
                    "worklogs":
                    [
                        {
                            "self": "https://jira.com/rest/api/2/issue/1818171/worklog/1803237",
                            "author":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "updateAuthor":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "created": "2022-07-22T08:02:13.000-0400",
                            "updated": "2022-07-22T08:02:13.000-0400",
                            "started": "2022-07-20T10:41:39.000-0400",
                            "timeSpent": "1d",
                            "timeSpentSeconds": 28800,
                            "id": "1803237",
                            "issueId": "1818171"
                        }
                    ]
                }
            }
        }
    ]
}
                            """.trimIndent()
                        )
                )
        )
        WebclientExistingTimeLogProvider(
            baseUrl = wireMockServer.baseUrl(),
            user = "foo",
            password = "bar",
            webClientBuilder = webclientBuilder
        ).howManyHoursLoggedAlready(
            OffsetDateTime.of(2022, 7, 20, 0, 0, 0, 0, ZoneOffset.UTC)
        ) shouldBe Duration.ofHours(8)
    }

    @Test
    fun `should return 8h duration for three worklogs`() {
        val wireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())
        wireMockServer.start()
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/rest/api/2/search"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
{
    "expand": "schema,names",
    "startAt": 0,
    "maxResults": 50,
    "total": 2,
    "issues":
    [
        {
            "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
            "id": "1813884",
            "self": "https://jira.com/rest/api/2/issue/1813884",
            "key": "IPA-1602",
            "fields":
            {
                "worklog":
                {
                    "startAt": 0,
                    "maxResults": 20,
                    "total": 2,
                    "worklogs":
                    [
                        {
                            "self": "https://jira.com/rest/api/2/issue/1813884/worklog/1800940",
                            "author":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "updateAuthor":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "created": "2022-07-08T15:51:51.000-0400",
                            "updated": "2022-07-08T15:51:51.000-0400",
                            "started": "2022-06-28T08:49:39.000-0400",
                            "timeSpent": "4h",
                            "timeSpentSeconds": 14400,
                            "id": "1800940",
                            "issueId": "1813884"
                        },
                        {
                            "self": "https://jira.com/rest/api/2/issue/1813884/worklog/1801665",
                            "author":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "updateAuthor":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "created": "2022-07-14T09:48:59.000-0400",
                            "updated": "2022-07-14T09:48:59.000-0400",
                            "started": "2022-07-14T11:14:23.000-0400",
                            "timeSpent": "1d",
                            "timeSpentSeconds": 28800,
                            "id": "1801665",
                            "issueId": "1813884"
                        }
                    ]
                }
            }
        },
        {
            "expand": "operations,versionedRepresentations,editmeta,changelog,renderedFields",
            "id": "1803314",
            "self": "https://jira.com/rest/api/2/issue/1803314",
            "key": "IPA-1587",
            "fields":
            {
                "worklog":
                {
                    "startAt": 0,
                    "maxResults": 20,
                    "total": 1,
                    "worklogs":
                    [
                        {
                            "self": "https://jira.com/rest/api/2/issue/1803314/worklog/1800941",
                            "author":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "updateAuthor":
                            {
                                "self": "https://jira.com/rest/api/2/user?username=kocelkr",
                                "name": "kocelkr",
                                "key": "kocelkr",
                                "emailAddress": "Krzysztof.Kocel@vimn.com",
                                "avatarUrls":
                                {
                                    "48x48": "https://jira.com/secure/useravatar?",
                                    "24x24": "https://jira.com/secure/useravatar?size=small&",
                                    "16x16": "https://jira.com/secure/useravatar?size=xsmall&",
                                    "32x32": "https://jira.com/secure/useravatar?size=medium&"
                                },
                                "displayName": "Kocel, Krzysztof",
                                "active": true,
                                "timeZone": "US/Eastern"
                            },
                            "created": "2022-07-08T15:51:52.000-0400",
                            "updated": "2022-07-08T15:51:52.000-0400",
                            "started": "2022-06-28T08:49:39.000-0400",
                            "timeSpent": "4h",
                            "timeSpentSeconds": 14400,
                            "id": "1800941",
                            "issueId": "1803314"
                        }
                    ]
                }
            }
        }
    ]
}
                            """.trimIndent()
                        )
                )
        )
        WebclientExistingTimeLogProvider(
            baseUrl = wireMockServer.baseUrl(),
            user = "foo",
            password = "bar",
            webClientBuilder = webclientBuilder
        ).howManyHoursLoggedAlready(
            OffsetDateTime.of(2022, 6, 28, 0, 0, 0, 0, ZoneOffset.UTC)
        ) shouldBe Duration.ofHours(8)
    }
}
