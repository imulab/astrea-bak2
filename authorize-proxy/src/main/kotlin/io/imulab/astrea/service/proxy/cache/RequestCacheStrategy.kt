package io.imulab.astrea.service.proxy.cache

interface RequestCacheStrategy {

    suspend fun put(params: Map<String, String>): String

    suspend fun get(id: String): Map<String, String>

    suspend fun evict(id: String)

    suspend fun getAndEvict(id: String): Map<String, String> = get(id).also { evict(id) }
}