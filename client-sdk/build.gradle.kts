import build.*
import com.google.protobuf.gradle.*
import groovy.lang.GroovyObject
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig

plugins {
    java
    idea
    kotlin("jvm")
    id("com.google.protobuf")
    id("jacoco")
    `maven-publish`
    id("com.jfrog.artifactory")
}

group = "io.imulab.astrea.sdk"
version = "0.0.1"

dependencies {
    kotlin(loadCoroutine = true)
    grpc()
    projects(loadOAuthSdk = true, loadOidcSdk = true)
    test(loadSpek2 = true, loadMockitoKotlin = true, loadAssertj = true)
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

publishing {
    publications {
        create<MavenPublication>("client-sdk") {
            from(components["java"])
            artifactId = "client-sdk"
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
            invokeMethod("publications", "client-sdk")
        })
    })
}