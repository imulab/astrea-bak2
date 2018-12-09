import build.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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

    kotlinAndCoroutine()
    grpc()
    grpcServerSide()
    springDefaultBundle()
    springGrpc()
    springMongo()

    jBCrypt()
    jose4j()

    junitPlatform()
    spek2()
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
}