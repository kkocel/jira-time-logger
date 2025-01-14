pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }

    val sbVersion = providers.gradleProperty("sbVersion").getOrElse("3.4.0")
    plugins {
        id("org.springframework.boot") version sbVersion
    }
}
rootProject.name = "jira-time-logger"
