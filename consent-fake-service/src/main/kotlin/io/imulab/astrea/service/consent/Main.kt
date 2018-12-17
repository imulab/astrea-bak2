package io.imulab.astrea.service.consent

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import okhttp3.HttpUrl
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jwk.Use
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.slf4j.LoggerFactory

private val rootLogger = LoggerFactory.getLogger("io.imulab.astrea.service.consent.Main")

fun main() {
    val vertx = Vertx.vertx(VertxOptions())

    vertx.deployVerticle(ConsentVerticle(ConfigFactory.load())) { ar ->
        if (ar.succeeded()) {
            rootLogger.info("Successfully deployed verticle with id ${ar.result()}")
        } else {
            rootLogger.error("Failed to deploy verticle.", ar.cause())
            System.exit(1)
        }
    }
}

class ConsentVerticle(private val appConfig: Config) : AbstractVerticle() {

    private val signingKey = JsonWebKeySet(appConfig.getString("service.key"))
        .findJsonWebKey(null, null, Use.SIGNATURE, AlgorithmIdentifiers.RSA_USING_SHA256)
        .also { checkNotNull(it) } as RsaJsonWebKey

    override fun start() {
        val router = Router.router(vertx).apply {
            get("/").handler { rc ->
                val consentToken = JsonWebSignature().apply {
                    key = signingKey.rsaPrivateKey
                    keyIdHeaderValue = signingKey.keyId
                    algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
                    payload = JwtClaims().also { c ->
                        c.setGeneratedJwtId()
                        c.setExpirationTimeMinutesInTheFuture(60f)
                        c.setIssuedAtToNow()
                        c.issuer = appConfig.getString("service.url")
                        c.setAudience(appConfig.getString("authorizeProxy.name"))
                        c.setClaim("scope", rc.request().getParam("scope"))
                    }.toJson()
                }.compactSerialization

                val url = HttpUrl.parse(appConfig.getString("authorizeProxy.url"))!!
                    .newBuilder()
                    .apply { rc.request().params().forEach { e -> addQueryParameter(e.key, e.value) } }
                    .apply { addQueryParameter("consent_token", consentToken) }
                    .build()
                    .toString()

                rc.response().setStatusCode(302).putHeader("Location", url).end()
            }
        }

        vertx.createHttpServer(HttpServerOptions().apply {
            port = appConfig.getInt("service.port")
        }).requestHandler(router).listen()
    }
}