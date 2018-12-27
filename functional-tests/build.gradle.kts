@file:Suppress("UnstableApiUsage")

import build.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

group = "io.imulab.astrea.test"
version = "0.0.1-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    kotlin(loadReflect = true)
    okHttp()
    typeSafeConfig()
    logging()
    test(loadJUnit5 = true, loadAssertj = true, loadSpek2 = true)
    testContainers(loadJUnitJupiter = true, loadDockerCompose = true)
}
