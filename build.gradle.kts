import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.BIN
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val arrowVersion = "2.0.1"
val kotlinLoggingVersion = "3.0.5"
val kotlinTestVersion = "5.9.1"
val wireMockVersion = "3.11.0"

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management") version "1.1.7"
    val kotlinVersion = "2.1.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("com.google.osdetector") version "1.7.3"
    id("org.jmailen.kotlinter") version "5.0.1"
    id("io.gitlab.arturbosch.detekt") version ("1.23.7")
}

group = "tech.kocel"
version = "0.0.1-SNAPSHOT"

project.afterEvaluate {
    configurations["detekt"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(getSupportedKotlinVersion())
        }
    }
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(JvmTarget.JVM_21)
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
    testImplementation("org.wiremock:wiremock-standalone:$wireMockVersion")
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
