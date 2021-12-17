package com.shoshin.common.default_responses

import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.internalServerError() {
    respond(
        status = HttpStatusCode.InternalServerError,
        ErrorResponse(ApiError(message = "InternalServerError"))
    )
}