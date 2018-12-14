import build.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    kotlin("jvm")
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
    id("io.spring.dependency-management")
}

group = "io.imulab.astrea.service.proxy"
version = "0.0.1-SNAPSHOT"

repositories {
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Version.springCloud}")
    }
}

dependencies {
    implementation(project(":oauth-sdk"))
    implementation(project(":oidc-sdk"))
    implementation(project(":client-sdk"))
    implementation(project(":discovery-sdk"))

    kotlin()
    grpc()
    jBCrypt()
    jose4j()

    springBoot(
        loadKotlinSupport = true,
        loadTestSupport = true,
        loadZuul = true
    )

    resilience4j(loadRetry = true)

    test(
        loadSpek2 = true,
        loadMockitoKotlin = true,
        loadAssertj = true
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}