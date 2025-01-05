import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2" apply false
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }
}

object Versions {
    const val KTOR_CLIENT = "3.0.3"
    const val COROUTINES = "1.9.0"
    const val SPRINGDOC = "2.7.0"
}

dependencies {
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // spring boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.SPRINGDOC}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${Versions.SPRINGDOC}")
    implementation("org.springdoc:springdoc-openapi-starter-common:${Versions.SPRINGDOC}")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.COROUTINES}")

    // ktor
    implementation("io.ktor:ktor-client-core:${Versions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-client-cio:${Versions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-client-content-negotiation:${Versions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.KTOR_CLIENT}")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
    workerMaxHeapSize.set("512m")
}

configure<KtlintExtension> {
    version.set("0.50.0")
    enableExperimentalRules.set(true)
}
