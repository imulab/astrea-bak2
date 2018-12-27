package io.imulab.astrea.service

import com.typesafe.config.Config
import io.vertx.core.Vertx
import org.kodein.di.Kodein

open class Components(private val vertx: Vertx, private val config: Config) {

    open fun bootstrap(): Kodein {
        return Kodein {

        }
    }
}