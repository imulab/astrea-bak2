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

    kotlin(loadCoroutine = true)
    kodein(loadErased = false)

    vertx(
        loadWeb = true,
        loadWebClient = true,
        loadWebApiContract = true,
        loadCoroutineSupport = true,
        loadConfig = true,
        loadMongoClient = true,
        loadHealthCheck = true
    )

    grpc(loadNetty = true)

    jBCrypt()
    jose4j()

    test(
        loadSpek2 = true,
        loadMockitoKotlin = true,
        loadAssertj = true
    )

    // throw away
    springDefaultBundle()
    springWebMvc()
    springGrpc()
    springMongo()
}