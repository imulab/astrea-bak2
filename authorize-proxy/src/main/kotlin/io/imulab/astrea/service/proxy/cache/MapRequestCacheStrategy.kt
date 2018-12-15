package io.imulab.astrea.service.proxy.cache

import io.imulab.astrea.sdk.oauth.error.ServerError
import java.util.*

class MapRequestCacheStrategy : RequestCacheStrategy {

    private val cache = mutableMapOf<String, Map<String, String>>()

    override suspend fun put(params: Map<String, String>): String {
        val id = UUID.randomUUID().toString()
        cache[id] = params
        return id
    }

    override suspend fun get(id: String): Map<String, String> {
        return cache[id] ?: throw ServerError.internal("No previous request was made.")
    }

    override suspend fun evict(id: String) {
        cache.remove(id)
    }
}