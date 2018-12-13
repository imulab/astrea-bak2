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

repositories {
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation(project(":oauth-sdk"))
    implementation(project(":oidc-sdk"))
    implementation(project(":client-sdk"))

    kotlin(loadCoroutine = true)
    kodein(loadErased = false)

    vertx(
        loadWeb = true,
        loadWebClient = true,
        loadWebApiContract = true,
        loadCoroutineSupport = true,
        loadMongoClient = true,
        loadHealthCheck = true,
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

application {
    mainClassName = "io.imulab.astrea.service.client.MainKt"
}

tasks.withType<ShadowJar> {
    classifier = ""
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}