import build.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "io.imulab.astrea.service"
version = "0.0.1-SNAPSHOT"

dependencies {
    kotlin()
    vertx(loadWeb = true)
    typeSafeConfig()
    jose4j()
    okHttp()
    logging()
}

application {
    mainClassName = "io.imulab.astrea.service.login.MainKt"
}

tasks.withType<ShadowJar> {
    classifier = ""
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}