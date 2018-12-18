import build.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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
    kotlin(loadCoroutine = true)

    kodein()
    grpc(loadNetty = true)
    typeSafeConfig()
    resilience4j(loadRetry = true)
    logging()
    okHttp()
    vertx(
        loadWeb = true,
        loadCoroutineSupport = true,
        loadRedisClient = true,
        loadGrpc = true,
        loadHealthCheck = true
    )

    implementation(project(":oauth-sdk"))
    implementation(project(":oidc-sdk"))
    implementation(project(":client-sdk"))
    implementation(project(":discovery-sdk"))

    test(
        loadJUnit5 = true,
        loadSpek2 = true,
        loadAssertj = true,
        loadMockitoKotlin = true
    )
}

application {
    mainClassName = "io.imulab.astrea.service.authorize.MainKt"
}

tasks.withType<ShadowJar> {
    classifier = ""
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}