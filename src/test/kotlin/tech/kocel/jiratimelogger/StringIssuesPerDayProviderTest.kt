package tech.kocel.jiratimelogger

import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class StringIssuesPerDayProviderTest {
    @Test
    fun `should parse sample input`() {
        val input = """
            2022-05-27 11:14:11 [voice-pluto-podcasts:fix/IPA-666-foo] IPA-666 Foo
            2022-05-27 11:21:28 [voice-pluto-podcasts:feature/IPA-1548-channels] Merge branch 'master' into feature/IPA-1548-channels
 # Conflicts:
 #	src/main/kotlin/tech/viacbs/plutopodcasts/core/provider/art19/Art19Retriever.kt
 #	src/main/kotlin/tech/viacbs/plutopodcasts/core/schedule/SchedulePrefetcher.kt
 #	src/main/kotlin/tech/viacbs/plutopodcasts/core/store/InMemoryScheduleStore.kt
 #	src/main/kotlin/tech/viacbs/plutopodcasts/core/store/RedisScheduleStore.kt
 #	src/main/kotlin/tech/viacbs/plutopodcasts/skill/content/ChannelContentProvider.kt
 #	src/main/kotlin/tech/viacbs/plutopodcasts/skill/logic/WelcomeLogic.kt
 #	src/test/kotlin/tech/viacbs/plutopodcasts/skill/alexa/ResponseExtensions.kt  
            2022-06-21 14:10:45 [voice-pluto-podcasts:fix/IPA-1597-add-metadata-if-present] IPA-1597 IPA-1600 Do not add metadata if not present in the clip  
            2022-06-21 21:24:11 [voice-pluto-podcasts:fix/fire-tv-controls] Handling of fire tv remote 
            2022-06-22 15:33:25 [voice-daily-show:fix/IPA-1598-playlist-memory-issue] IPA-1598 Fix redis playlist deserialization issue, removed auto advance  
            2022-06-23 13:25:52 [voice-pluto-podcasts:feature/IPA-1550-reporting] IPA-1550 IPA-1550 Player events rename  
            2022-06-23 15:11:03 [voice-pluto-podcasts:feature/new-events] IPA-1550 IPA-1550 Player new events  
        """.lines()
            .asSequence()

        val output = StringIssuesPerDayProvider().provideDaysWithIssues(input)

        output shouldContainExactly listOf(
            DayWithIssues(
                day = LocalDateTime.of(2022, 5, 27, 11, 14, 11).atOffset(UTC),
                issues = listOf("IPA-666", "IPA-1548")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 21, 14, 10, 45).atOffset(UTC),
                issues = listOf("IPA-1597", "IPA-1600")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 22, 15, 33, 25).atOffset(UTC),
                issues = listOf("IPA-1598")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 23, 13, 25, 52).atOffset(UTC),
                issues = listOf("IPA-1550", "IPA-1550")
            )
        )
    }
}
