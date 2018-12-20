import build.grpc
import build.kotlin
import build.test
import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "io.imulab.astrea.sdk"
version = "0.0.1-SNAPSHOT"

dependencies {
    kotlin(loadCoroutine = true)
    grpc()
    test(loadSpek2 = true, loadMockitoKotlin = true, loadAssertj = true)

    implementation(project(":oauth-sdk"))
    implementation(project(":oidc-sdk"))
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