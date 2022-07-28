package tech.kocel.jiratimelogger

import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class StringIssuesPerDayProviderTest {
    @Test
    fun `should parse sample input`() {
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
        """.lines()
            .asSequence()

        val output = StringIssuesPerDayProvider().provideDaysWithIssues(input)

        output shouldContainExactly listOf(
            DayWithIssues(
                day = LocalDateTime.of(2022, 5, 27, 11, 14, 11).atOffset(UTC),
                issues = listOf("FOO-666", "FOO-1548")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 21, 14, 10, 45).atOffset(UTC),
                issues = listOf("FOO-1597", "FOO-1600")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 22, 15, 33, 25).atOffset(UTC),
                issues = listOf("FOO-1598")
            ),
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 23, 13, 25, 52).atOffset(UTC),
                issues = listOf("FOO-1550", "FOO-1550")
            )
        )
    }
}
