package com.shoshin.common.default_responses

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.notFound(message: String? = "Not found") {
    respond(
        HttpStatusCode.NotFound,
        message ?: "Not found"
    )
}