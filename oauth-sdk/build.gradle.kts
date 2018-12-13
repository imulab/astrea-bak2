import build.*
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm")
    id("jacoco")
}

group = "io.imulab.astrea.sdk"
version = "0.1.0"

dependencies {
    kotlinAndCoroutine()
    api("org.mindrot:jbcrypt:${Version.jBCrypt}")
    api("org.bitbucket.b_c:jose4j:${Version.jose4j}")

    junitPlatform()
    spek2()
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

jacoco {
    toolVersion = "0.8.2"
}