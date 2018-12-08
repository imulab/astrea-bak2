import build.junitPlatform
import build.kotlinAndCoroutine
import build.spek2
import build.grpc
import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    kotlin("jvm")
    id("com.google.protobuf") version "0.8.7"
    id("jacoco")
}

group = "io.imulab.astrea.sdk"
version = "0.0.1"

dependencies {
    kotlinAndCoroutine()
    grpc()
    implementation(project(":oauth"))
    implementation(project(":oidc"))

    junitPlatform()
    spek2()
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

protobuf.protobuf.run {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/src/generated"
}

jacoco {
    toolVersion = "0.8.2"
}