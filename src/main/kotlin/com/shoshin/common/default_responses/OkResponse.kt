package com.shoshin.common.default_responses

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.ok(message: String = "Ok") {
    respond(
        respond(
            HttpStatusCode.OK,
            message
        )
    )
}