import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.BIN

val arrowVersion = "1.2.0"
val kotlinLoggingVersion = "3.0.5"
val kotlinTestVersion = "5.7.2"
val wireMockVersion = "3.0.1"

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    val kotlinVersion = "1.9.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("com.google.osdetector") version "1.7.3"
    id("org.jmailen.kotlinter") version "3.16.0"
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
}

group = "tech.kocel"
version = "0.0.1-SNAPSHOT"

project.afterEvaluate {
    configurations["detekt"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.8.21")
        }
    }
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    if (osdetector.arch.equals("aarch_64")) {
        implementation("io.netty:netty-all")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wireMockVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotlinTestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotlinTestVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.wrapper {
    gradleVersion = "8.2"
    distributionType = BIN
}
