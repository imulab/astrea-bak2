import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import build.*
import com.google.protobuf.gradle.*
import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig

plugins {
    java
    kotlin("jvm")
    id("com.google.protobuf")
    `maven-publish`
    id("com.jfrog.artifactory")
}

group = "io.imulab.astrea.sdk"
version = "0.0.1"

dependencies {
    kotlin()
    grpc()

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

publishing {
    publications {
        create<MavenPublication>("dicovery-sdk") {
            from(components["java"])
            artifactId = "dicovery-sdk"
        }
    }
}

artifactory {
    setContextUrl(System.getenv("ARTIFACTORY_CONTEXT_URL") ?: "nourl")
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", System.getenv("ARTIFACTORY_REPO") ?: "norepo")
            setProperty("username", System.getenv("ARTIFACTORY_USERNAME") ?: "nouser")
            setProperty("password", System.getenv("ARTIFACTORY_PASSWORD") ?: "nopass")
            setProperty("maven", true)
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", "dicovery-sdk")
        })
    })
}