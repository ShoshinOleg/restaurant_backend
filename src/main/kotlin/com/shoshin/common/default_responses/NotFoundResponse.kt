package com.shoshin.common.default_responses

import com.shoshin.common.ErrorInfo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.notFound(message: String = "Not found") {
    respond(
        status = HttpStatusCode.NotFound,
        ErrorInfo(
            code = HttpStatusCode.NotFound.value,
            message = message
        )
    )
}