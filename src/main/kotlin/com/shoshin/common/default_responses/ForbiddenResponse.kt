package com.shoshin.common.default_responses

import com.shoshin.common.ErrorInfo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.forbidden(message: String = "Don't have required permissions") {
    respond(
        status = HttpStatusCode.Forbidden,
        ErrorInfo(
            code = HttpStatusCode.Forbidden.value,
            message = message
        )
    )
}