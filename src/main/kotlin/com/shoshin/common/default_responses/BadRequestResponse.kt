package com.shoshin.common.default_responses

import com.shoshin.common.ErrorInfo
import com.shoshin.common.exceptions.BadRequestError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.badRequest(errorMessage: String = "BadRequest") {
    respond(
        status = HttpStatusCode.BadRequest,
        ErrorInfo(
            code = HttpStatusCode.BadRequest.value,
            message = errorMessage
        )
    )
}