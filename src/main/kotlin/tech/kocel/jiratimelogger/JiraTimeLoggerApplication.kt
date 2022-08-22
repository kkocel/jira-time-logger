package tech.kocel.jiratimelogger

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

@SpringBootApplication
class JiraTimeLoggerApplication

fun main(args: Array<String>) {
    val initConfig: SpringApplication.() -> Unit = {
        webApplicationType = WebApplicationType.NONE
        addInitializers(BeansInitializer())
    }

    runApplication<JiraTimeLoggerApplication>(args = args, init = initConfig)
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    @Suppress("LongMethod")
    override fun initialize(applicationContext: GenericApplicationContext) {
        beans {
            bean<LogTimeSpringRunner>()
            bean<IssueFileLogCrawler>()
            bean<EqualIssueTimePartitioner>()
            bean<StringIssuesPerDayProvider>()
            bean<LoggingOrchestrator>()
            bean {
                JiraWorkLogger(
                    baseUrl = env.getRequiredProperty("jira.host"),
                    user = env.getRequiredProperty("jira.user"),
                    password = env.getRequiredProperty("jira.password"),
                    webClientBuilder = ref()
                )
            }
            bean {
                JiraExistingTimeLogProvider(
                    baseUrl = env.getRequiredProperty("jira.host"),
                    user = env.getRequiredProperty("jira.user"),
                    password = env.getRequiredProperty("jira.password"),
                    webClientBuilder = ref()
                )
            }
        }.initialize(applicationContext)
    }
}
