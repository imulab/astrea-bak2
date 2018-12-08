import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import build.*

plugins {
    java
    kotlin("jvm") version "1.3.10" apply false
    id("com.google.protobuf") version "0.8.7" apply false
    id("com.gradle.build-scan") version "1.16"
    id("com.jfrog.artifactory") version "4.8.1" apply false
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    group = "io.imulab.astrea"

    repositories {
        jcenter()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/spekframework/spek") }
    }
}

subprojects {
    apply(plugin = "idea")

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
            suppressWarnings = true
        }
    }

    tasks.withType<Test>().all {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    tasks.withType<JacocoReport>().all {
        reports {
            html.isEnabled = true
            html.destination = file("$buildDir/reports/jacoco/html")
        }
    }
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}