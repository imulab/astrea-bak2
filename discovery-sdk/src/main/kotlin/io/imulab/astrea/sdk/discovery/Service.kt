package io.imulab.astrea.sdk.discovery

import io.grpc.Channel
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import java.util.concurrent.TimeUnit

/**
 * Service interface for retrieving a discovery object.
 */
interface DiscoveryService {
    suspend fun getDiscovery(): Discovery
}

class GrpcDiscoveryService(channel: Channel) : DiscoveryService {

    private val stub = DiscoveryGrpc.newBlockingStub(channel)

    override suspend fun getDiscovery(): Discovery {
        val response = try {
            stub.withDeadlineAfter(30, TimeUnit.SECONDS).get(DiscoveryRequest.getDefaultInstance())
        } catch (e : Exception) {
            throw ServerError.internal("Cannot obtain discovery configuration.")
        }

        return try {
            io.imulab.astrea.sdk.discovery.Discovery.fromDiscoveryResponse(response)
        } catch (e: Exception) {
            throw ServerError.internal(e.localizedMessage)
        }
    }
}