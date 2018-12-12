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
    projects(
        loadOAuthSdk = true,
        loadOidcSdk = true,
        loadClientSdk = true
    )

    kotlin(
        loadCoroutine = true
    )

    vertx(
        loadWeb = true,
        loadWebClient = true,
        loadWebApiContract = true,
        loadConfig = true,
        loadMongoClient = true,
        loadHealthCheck = true
    )

    grpc(
        loadNetty = true
    )

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