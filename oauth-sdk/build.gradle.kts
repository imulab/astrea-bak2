import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "io.imulab.astrea.sdk"
version = "0.1.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.bitbucket.b_c:jose4j:0.6.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
}