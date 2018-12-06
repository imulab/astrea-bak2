import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "io.imulab.astrea.sdk"
version = "0.1.0"

repositories {
    maven {
        url = uri("https://dl.bintray.com/spekframework/spek")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    testRuntimeOnly(kotlin("reflect"))

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.bitbucket.b_c:jose4j:0.6.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")

    testImplementation("org.assertj:assertj-core:3.11.1")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.0-rc.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation("org.spekframework.spek2:spek-runner-junit5:2.0.0-rc.1") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.junit.platform")
    }
}

tasks.withType<Test> {
    println("Configuring $name in project ${project.name}")
    useJUnitPlatform {
        includeEngines("spek2")
    }
}