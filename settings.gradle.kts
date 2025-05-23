pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }

    var sbVersion = "3.5.0"
    sbVersion = providers.gradleProperty("sbVersion").getOrElse(sbVersion)
    plugins {
        id("org.springframework.boot") version sbVersion
    }
}
rootProject.name = "jira-time-logger"
