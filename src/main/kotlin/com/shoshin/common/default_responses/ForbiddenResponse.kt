package com.shoshin.common.default_responses

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.forbidden(message: String = "Don't have required permissions") {
    respond(
        status = HttpStatusCode.Forbidden,
        message
    )
}