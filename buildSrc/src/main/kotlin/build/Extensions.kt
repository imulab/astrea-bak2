package build

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.kotlin


fun DependencyHandler.kotlinAndCoroutine() {
    add(implementation, kotlin("stdlib-jdk8"))
    add(implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.kotlinCoroutine}")
}

fun DependencyHandler.grpc2() {
    add(implementation, "com.google.protobuf:protobuf-java:${Version.protobuf}")
    add(implementation, "io.grpc:grpc-stub:${Version.grpc}")
    add(implementation, "io.grpc:grpc-protobuf:${Version.grpc}")
}

fun DependencyHandler.grpcServerSide() {
    add(implementation, "io.grpc:grpc-netty:${Version.grpc}")
}

fun DependencyHandler.junitPlatform() {
    add(testImplementation, "org.junit.jupiter:junit-jupiter-api:${Version.junit}")
    add(testRuntimeOnly, "org.junit.jupiter:junit-jupiter-engine:${Version.junit}")
}

fun DependencyHandler.spek2() {
    add(testImplementation, "org.spekframework.spek2:spek-dsl-jvm:${Version.spek2}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    add(testImplementation, "org.spekframework.spek2:spek-runner-junit5:${Version.spek2}") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.junit.platform")
    }
    add(testRuntimeOnly, kotlin("reflect"))
}

fun DependencyHandler.springDefaultBundle() {
    add(implementation, "org.springframework.boot:spring-boot-starter-actuator")
    add(implementation, "com.fasterxml.jackson.module:jackson-module-kotlin")
    add(runtimeOnly, "org.springframework.boot:spring-boot-devtools")
    add(compileOnly, "org.springframework.boot:spring-boot-configuration-processor")
    add(testImplementation, "org.springframework.boot:spring-boot-starter-test")
}

fun DependencyHandler.springWebMvc() {
    add(implementation, "org.springframework.boot:spring-boot-starter-web")
}

fun DependencyHandler.springWebFlux() {
    add(implementation, "org.springframework.boot:spring-boot-starter-webflux")
}

fun DependencyHandler.springGrpc() {
    add(implementation, "io.github.lognet:grpc-spring-boot-starter:${Version.lognetSpringGrpc}") {
        exclude(group = "io.grpc", module = "grpc-netty")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter")
    }
}

fun DependencyHandler.springMongo() {
    add(implementation, "org.springframework.boot:spring-boot-starter-data-mongodb")
    add(testImplementation, "de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

fun DependencyHandler.springSessionRedis() {
    add(implementation, "org.springframework.session:spring-session-data-redis")
}

fun DependencyHandler.springSessionReactiveRedis() {
    add(implementation, "org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

fun DependencyHandler.jBCrypt() {
    add(implementation, "org.mindrot:jbcrypt:${Version.jBCrypt}")
}

fun DependencyHandler.jose4j() {
    add(implementation, "org.bitbucket.b_c:jose4j:${Version.jose4j}")
}

fun DependencyHandler.mockito() {
    add(testImplementation, "com.nhaarman.mockitokotlin2:mockito-kotlin:${Version.mockitoKotlin}")
}

fun DependencyHandler.assertj() {
    add(testImplementation, "org.assertj:assertj-core:${Version.assertj}")
}