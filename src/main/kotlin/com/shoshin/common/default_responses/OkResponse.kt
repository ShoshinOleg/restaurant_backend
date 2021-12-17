package com.shoshin.common.default_responses

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend inline fun <reified T : Any>  ApplicationCall.ok(message: T) {
    respond(
        HttpStatusCode.OK,
        message
    )
}

suspend fun ApplicationCall.ok() {
    respond(
        HttpStatusCode.OK,
        "OK"
    )
}