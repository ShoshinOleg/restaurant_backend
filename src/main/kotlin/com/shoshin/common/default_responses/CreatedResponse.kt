package com.shoshin.common.default_responses

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend inline fun <reified T : Any> ApplicationCall.created(message : T) {
    respond(
        HttpStatusCode.Created,
        message
    )
}