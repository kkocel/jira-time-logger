package tech.kocel.jiratimelogger

import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

class RemainingCommitsReaderTest {
    @Test
    fun `should generate days with issues form sample commits input`() {
        // given
        val input = """
            2022-05-27 11:14:11 [some-project:fix/FOO-666-foo] FOO-666 Foo
            2022-05-27 11:21:28 [some-project:feature/FOO-1548-channels] Merge branch 'master' into fe/FOO-1548-channels
 # Conflicts:
 #	src/main/kotlin/foo/bar/some/file/File.kt
            2022-06-21 14:10:45 [some-project:fix/FOO-1597-add-metadata] FOO-1597 FOO-1600 Do not add metadata
            2022-06-21 21:24:11 [some-project:fix/remote-code-execution] Handling of code remote execution fix
            2022-06-22 15:33:25 [some-other-project:fix/FOO-1598-memory-issue] FOO-1598 Fix memory issue 
            2022-06-23 13:25:52 [some-project:feature/FOO-1550-reporting] FOO-1550 FOO-1550 Events rename  
            2022-06-23 15:11:03 [some-project:feature/new-events] FOO-1550 FOO-1550 New events  
        """

        val commitLogFilePath =
            createTempFile().apply {
                writeText(input)
            }

        val output =
            RemainingCommitsReader(
                clock = Clock.fixed(Instant.parse("2022-05-25T16:45:42.00Z"), UTC),
                logfileName = commitLogFilePath.absolutePathString(),
                finishDayMarkerFileName = createTempFile().absolutePathString()
            ).provideRemainingDaysWithIssues()

        output shouldContainExactly
            listOf(
                DayWithIssues(
                    day = dateTime(2022, 5, 27, 11, 14, 11),
                    issues = listOf("FOO-666", "FOO-1548")
                ),
                DayWithIssues(
                    day = dateTime(2022, 6, 21, 14, 10, 45),
                    issues = listOf("FOO-1597", "FOO-1600")
                ),
                DayWithIssues(
                    day = dateTime(2022, 6, 22, 15, 33, 25),
                    issues = listOf("FOO-1598")
                ),
                DayWithIssues(
                    day = dateTime(2022, 6, 23, 13, 25, 52),
                    issues = listOf("FOO-1550", "FOO-1550")
                )
            )
    }

    @Suppress("LongParameterList")
    private fun dateTime(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int
    ): OffsetDateTime = LocalDateTime.of(year, month, day, hour, minute, second).atOffset(UTC)
}
