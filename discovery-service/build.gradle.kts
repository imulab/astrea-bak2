import build.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "io.imulab.astrea.service"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":oauth-sdk"))
    implementation(project(":oidc-sdk"))
    implementation(project(":discovery-sdk"))

    kotlin()
    vertx(
        loadWeb = true,
        loadGrpc = true
    )
    grpc(loadNetty = true)
    typeSafeConfig()
    jBCrypt()
    jose4j()

    logging()

    test(
        loadSpek2 = true,
        loadMockitoKotlin = true,
        loadAssertj = true
    )
}