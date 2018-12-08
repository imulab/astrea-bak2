import build.Version
import build.junitPlatform
import build.kotlinAndCoroutine
import build.spek2
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("jacoco")
}

group = "io.imulab.astrea.sdk"
version = "0.1.0"

dependencies {
    kotlinAndCoroutine()
    api(project(":oauth-sdk"))
    implementation("org.mindrot:jbcrypt:${Version.jBCrypt}")
    implementation("org.bitbucket.b_c:jose4j:${Version.jose4j}")

    junitPlatform()
    spek2()
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

jacoco {
    toolVersion = "0.8.2"
}