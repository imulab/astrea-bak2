package io.imulab.astrea.service.authorization

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NixApplication

fun main(args: Array<String>) {
    runApplication<NixApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}