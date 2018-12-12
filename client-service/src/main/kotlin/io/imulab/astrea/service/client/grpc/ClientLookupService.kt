package io.imulab.astrea.service.client.grpc

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.client.ClientLookupGrpc
import io.imulab.astrea.sdk.client.ClientLookupRequest
import io.imulab.astrea.sdk.client.ClientLookupResponse
import io.imulab.astrea.sdk.client.DummyClientLookupService

class ClientLookupService : ClientLookupGrpc.ClientLookupImplBase() {

    override fun find(request: ClientLookupRequest?, responseObserver: StreamObserver<ClientLookupResponse>?) {
        return DummyClientLookupService.find(request, responseObserver)
    }
}