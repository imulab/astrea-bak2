package build

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.kotlin

const val implementation = "implementation"
const val testImplementation = "testImplementation"
const val testRuntimeOnly = "testRuntimeOnly"
const val runtimeOnly = "runtimeOnly"
const val compileOnly = "compileOnly"

object Version {
    const val kotlin = "1.3.10"
    const val kotlinCoroutine = "1.0.1"
    const val jacksonKotlin = "2.9.7"
    const val junit = "5.3.1"
    const val spek2 = "2.0.0-rc.1"
    const val jose4j = "0.6.4"
    const val jBCrypt = "0.4"
    const val grpc = "1.17.1"
    const val protobuf = "3.6.1"
    const val springCloud = "Greenwich.RC1"
    const val lognetSpringGrpc = "3.0.0"
    const val mockitoKotlin = "2.0.0"
    const val assertj = "3.11.1"

    const val vertx = "3.6.0"
    const val kodein = "6.0.1"
    const val slf4j = "1.7.25"
    const val log4j = "2.11.1"
    const val typeSafeConfig = "1.3.2"
    const val resilience4j = "0.13.1"
    const val okHttp = "3.12.0"
    const val zuul = "2.1.2"

    const val testContainers = "1.10.3"
}

fun DependencyHandler.kotlin(
    loadCoroutine: Boolean = false,
    loadReflect: Boolean = false
) {
    add(implementation, kotlin("stdlib-jdk8"))

    if (loadCoroutine)
        add(implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.kotlinCoroutine}")
    if (loadReflect)
        add(implementation, "org.jetbrains.kotlin:kotlin-reflect")
}

fun DependencyHandler.kodein(
    loadErased: Boolean = false
) {
    if (loadErased)
        add(implementation, "org.kodein.di:kodein-di-generic-jvm:${Version.kodein}")
    else
        add(implementation, "org.kodein.di:kodein-di-generic-jvm:${Version.kodein}")
}

fun DependencyHandler.grpc(
    loadNetty: Boolean = false
) {
    add(implementation, "com.google.protobuf:protobuf-java:${Version.protobuf}")
    add(implementation, "io.grpc:grpc-stub:${Version.grpc}")
    add(implementation, "io.grpc:grpc-protobuf:${Version.grpc}")

    if (loadNetty)
        add(implementation, "io.grpc:grpc-netty:${Version.grpc}")
}

fun DependencyHandler.springBoot(
    loadKotlinSupport: Boolean = true,
    loadActuator: Boolean = false,
    loadDevTools: Boolean = false,
    loadConfigProcessor: Boolean = false,
    loadTestSupport: Boolean = true,
    loadWebMvc: Boolean = false,
    loadWebFlux: Boolean = false,
    loadGrpcSupport: Boolean = false,
    loadMongoSupport: Boolean = false,
    loadRedisSessionSupport: Boolean = false,
    loadZuul: Boolean = false
) {
    if (loadKotlinSupport)
        add(implementation, "com.fasterxml.jackson.module:jackson-module-kotlin")
    if (loadActuator)
        add(implementation, "org.springframework.boot:spring-boot-starter-actuator")
    if (loadDevTools)
        add(runtimeOnly, "org.springframework.boot:spring-boot-devtools")
    if (loadConfigProcessor)
        add(compileOnly, "org.springframework.boot:spring-boot-configuration-processor")
    if (loadTestSupport)
        add(testImplementation, "org.springframework.boot:spring-boot-starter-test")
    if (loadWebMvc)
        add(implementation, "org.springframework.boot:spring-boot-starter-web")
    if (loadWebFlux)
        add(implementation, "org.springframework.boot:spring-boot-starter-webflux")
    if (loadGrpcSupport)
        add(implementation, "io.github.lognet:grpc-spring-boot-starter:${Version.lognetSpringGrpc}") {
            exclude(group = "io.grpc", module = "grpc-netty")
            exclude(group = "org.springframework.boot", module = "spring-boot-starter")
        }
    if (loadMongoSupport) {
        add(implementation, "org.springframework.boot:spring-boot-starter-data-mongodb")
        add(testImplementation, "de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    }
    if (loadRedisSessionSupport) {
        add(implementation, "org.springframework.session:spring-session-data-redis")
        add(implementation, "org.springframework.boot:spring-boot-starter-data-redis")
    }
    if (loadZuul)
        add(implementation, "org.springframework.cloud:spring-cloud-starter-netflix-zuul")
}

fun DependencyHandler.test(
    loadJUnit5: Boolean = true,
    loadSpek2: Boolean = false,
    loadMockitoKotlin: Boolean = false,
    loadAssertj: Boolean = false
) {
    if (loadJUnit5) {
        add(testImplementation, "org.junit.jupiter:junit-jupiter-api:${Version.junit}")
        add(testRuntimeOnly, "org.junit.jupiter:junit-jupiter-engine:${Version.junit}")
    }

    if (loadSpek2) {
        add(testImplementation, "org.spekframework.spek2:spek-dsl-jvm:${Version.spek2}") {
            exclude(group = "org.jetbrains.kotlin")
        }
        add(testImplementation, "org.spekframework.spek2:spek-runner-junit5:${Version.spek2}") {
            exclude(group = "org.jetbrains.kotlin")
            exclude(group = "org.junit.platform")
        }
        add(testRuntimeOnly, kotlin("reflect"))
    }

    if (loadMockitoKotlin)
        add(testImplementation, "com.nhaarman.mockitokotlin2:mockito-kotlin:${Version.mockitoKotlin}")

    if (loadAssertj)
        add(testImplementation, "org.assertj:assertj-core:${Version.assertj}")
}

fun DependencyHandler.vertx(
    lang: String = "kotlin",
    loadWeb: Boolean = false,
    loadWebClient: Boolean = false,
    loadWebApiContract: Boolean = false,
    loadCoroutineSupport: Boolean = false,
    loadMongoClient: Boolean = false,
    loadRedisClient: Boolean = false,
    loadMysqlClient: Boolean = false,
    loadConfig: Boolean = false,
    loadCircuitBreaker: Boolean = false,
    loadHealthCheck: Boolean = false,
    loadGrpc: Boolean = false
) {
    add(implementation, "io.vertx:vertx-core:${Version.vertx}")

    when (lang) {
        "kotlin" -> {
            add(implementation, "io.vertx:vertx-lang-kotlin:${Version.vertx}")
            add(implementation, "com.fasterxml.jackson.module:jackson-module-kotlin:${Version.jacksonKotlin}")
        }
        "javascript" -> add(implementation, "io.vertx:vertx-lang-js:${Version.vertx}")
        "groovy" -> add(implementation, "io.vertx:vertx-lang-groovy:${Version.vertx}")
        else -> throw IllegalArgumentException("Unsupported vert.x language choice: $lang.")
    }

    if (loadWeb)
        add(implementation, "io.vertx:vertx-web:${Version.vertx}")
    if (loadWebClient)
        add(implementation, "io.vertx:vertx-web-client:${Version.vertx}")
    if (loadWebApiContract)
        add(implementation, "io.vertx:vertx-web-api-contract:${Version.vertx}")

    if (loadCoroutineSupport)
        add(implementation, "io.vertx:vertx-lang-kotlin-coroutines:${Version.vertx}")

    if (loadMongoClient)
        add(implementation, "io.vertx:vertx-mongo-client:${Version.vertx}")
    if (loadRedisClient)
        add(implementation, "io.vertx:vertx-redis-client:${Version.vertx}")
    if (loadMysqlClient)
        add(implementation, "io.vertx:vertx-mysql-postgresql-client:${Version.vertx}")

    if (loadConfig)
        add(implementation, "io.vertx:vertx-config:${Version.vertx}")
    if (loadCircuitBreaker)
        add(implementation, "io.vertx:vertx-circuit-breaker:${Version.vertx}")

    if (loadHealthCheck)
        add(implementation, "io.vertx:vertx-health-check:${Version.vertx}")

    if (loadGrpc)
        add(implementation, "io.vertx:vertx-grpc:${Version.vertx}")
}

fun DependencyHandler.typeSafeConfig() {
    add(implementation, "com.typesafe:config:${Version.typeSafeConfig}")
}

fun DependencyHandler.logging() {
    add(implementation, "org.slf4j:slf4j-api:${Version.slf4j}")
    add(implementation, "org.apache.logging.log4j:log4j-api:${Version.log4j}")
    add(implementation, "org.apache.logging.log4j:log4j-core:${Version.log4j}")
    add(implementation, "org.apache.logging.log4j:log4j-slf4j-impl:${Version.log4j}")
}

fun DependencyHandler.resilience4j(
    loadCircuitBreaker: Boolean = false,
    loadRateLimiter: Boolean = false,
    loadRetry: Boolean = false,
    loadBulkhead: Boolean = false,
    loadCache: Boolean = false,
    loadTimeLimiter: Boolean = false
) {
    if (loadCircuitBreaker)
        add(implementation, "io.github.resilience4j:resilience4j-circuitbreaker:0.13.1")
    if (loadRateLimiter)
        add(implementation, "io.github.resilience4j:resilience4j-ratelimiter:0.13.1")
    if (loadRetry)
        add(implementation, "io.github.resilience4j:resilience4j-retry:0.13.1")
    if (loadBulkhead)
        add(implementation, "io.github.resilience4j:resilience4j-bulkhead:0.13.1")
    if (loadCache)
        add(implementation, "io.github.resilience4j:resilience4j-cache:0.13.1")
    if (loadTimeLimiter)
        add(implementation, "io.github.resilience4j:resilience4j-timelimiter:0.13.1")
}

fun DependencyHandler.okHttp() {
    add(implementation, "com.squareup.okhttp3:okhttp:${Version.okHttp}")
}

fun DependencyHandler.netflix(loadZuul: Boolean = false) {
    if (loadZuul)
        add(implementation, "com.netflix.zuul:zuul-core:${Version.zuul}")
}

fun DependencyHandler.testContainers(
    loadMysql: Boolean = false,
    loadJUnitJupiter: Boolean = false,
    loadDockerCompose: Boolean = false
) {
    add(testImplementation, "org.testcontainers:testcontainers:${Version.testContainers}")
    if (loadMysql)
        add(testImplementation, "org.testcontainers:mysql:${Version.testContainers}")
    if (loadJUnitJupiter)
        add(testImplementation, "org.testcontainers:junit-jupiter:${Version.testContainers}")
    if (loadDockerCompose)
        add(testImplementation, "org.testcontainers:docker-compose:0.9.9")
}