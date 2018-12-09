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

group = "io.imulab.astrea.service"
version = "0.1.0"

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

    kotlinAndCoroutine()
    grpc()
    grpcServerSide()
    springDefaultBundle()
    springWebFlux()
    springGrpc()
    springSessionRedis()
    springSessionReactiveRedis()

    jBCrypt()
    jose4j()

    junitPlatform()
    spek2()
    mockito()
    assertj()
}
