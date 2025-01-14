package tech.kocel.jiratimelogger

import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import java.time.Duration.ofMinutes
import java.time.LocalDateTime
import java.time.ZoneOffset

class EqualIssueTimePartitionerTest {
    @Test
    fun `should partition six issues`() {
        // given
        val partitioner = EqualIssueTimePartitioner(InMemoryExistingTimeLogProvider())

        val dayWithIssues =
            DayWithIssues(
                day = LocalDateTime.of(2022, 6, 23, 8, 0).atOffset(ZoneOffset.UTC),
                issues = listOf("ISS-1", "ISS-1", "ISS-2", "ISS-3", "ISS-4", "ISS-5", "ISS-6", "ISS-6")
            )

        // when
        val issuesWithDuration = partitioner.howLongEachIssueTook(dayWithIssues)

        // then
        issuesWithDuration.map { it.loggedTime } shouldContainExactly
            listOf(
                ofMinutes(80),
                ofMinutes(80),
                ofMinutes(80),
                ofMinutes(80),
                ofMinutes(80),
                ofMinutes(80)
            )
    }

    @Test
    fun `should partition six issues with less time on each`() {
        // given
        val day =
            LocalDateTime
                .of(
                    2022,
                    6,
                    23,
                    8,
                    0
                ).atOffset(ZoneOffset.UTC)

        val partitioner =
            EqualIssueTimePartitioner(
                InMemoryExistingTimeLogProvider().apply {
                    addLog(day, ofMinutes(180))
                }
            )

        val dayWithIssues =
            DayWithIssues(
                day = day,
                issues = listOf("ISS-1", "ISS-1", "ISS-2", "ISS-3", "ISS-4", "ISS-5", "ISS-6", "ISS-6")
            )

        // when
        val issuesWithDuration = partitioner.howLongEachIssueTook(dayWithIssues)

        issuesWithDuration.map { it.loggedTime } shouldContainExactly
            listOf(
                ofMinutes(50),
                ofMinutes(50),
                ofMinutes(50),
                ofMinutes(50),
                ofMinutes(50),
                ofMinutes(50)
            )
    }
}
