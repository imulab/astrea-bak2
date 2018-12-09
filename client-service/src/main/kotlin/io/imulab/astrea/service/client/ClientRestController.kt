package io.imulab.astrea.service.client

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/client")
class ClientRestController {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createClient(@RequestBody request: ClientDTO): CreateClientResponse {
        return CreateClientResponse(
            id = "c1db4208-a023-42a5-ac47-a5cb94bb91c9",
            secret = "asdaldkalskasdaldjaldja"
        )
    }

    data class CreateClientResponse(val id: String, val secret: String)
}