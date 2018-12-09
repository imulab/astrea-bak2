package io.imulab.astrea.service.client.common

import io.imulab.astrea.sdk.client.Client
import org.springframework.data.repository.CrudRepository

interface ClientRepository : CrudRepository<Client, String> {

    fun countByName(name: String): Long
}