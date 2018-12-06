import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.10"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    group = "io.imulab.astrea"

    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    tasks.withType<KotlinCompile>().all {
        println("Configuring $name in project ${project.name}")
        kotlinOptions {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
            suppressWarnings = true
        }
    }
}