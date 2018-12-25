package io.imulab.astrea.service

import com.typesafe.config.Config
import io.vertx.core.Vertx
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

open class Components(private val vertx: Vertx, private val config: Config) {

    fun bootstrap(): Kodein {
        return Kodein {
            bind<GatewayVerticle>() with singleton {
                GatewayVerticle(
                    appConfig = config
                )
            }
        }
    }
}