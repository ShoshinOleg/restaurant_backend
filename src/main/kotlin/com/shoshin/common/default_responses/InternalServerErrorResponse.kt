package com.shoshin.common.default_responses

import com.shoshin.common.ErrorInfo
import com.shoshin.common.exceptions.InternalServerError
import com.shoshin.common.exceptions.StatusException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.internalServerError(
    errorMessage: String = "InternalServerError",
    throwable: Throwable? = null
) {
    respond(
        status = HttpStatusCode.InternalServerError,
        ErrorInfo(
            code = HttpStatusCode.InternalServerError.value,
            message = throwable?.message ?: errorMessage
        )
    )
}