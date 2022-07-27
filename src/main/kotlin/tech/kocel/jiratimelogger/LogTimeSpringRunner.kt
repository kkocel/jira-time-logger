package tech.kocel.jiratimelogger

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext

class LogTimeSpringRunner(
    private val loggingOrchestrator: LoggingOrchestrator,
    private val applicationContext: ApplicationContext
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        loggingOrchestrator.logTimeOnIssues()
        SpringApplication.exit(applicationContext)
    }
}
