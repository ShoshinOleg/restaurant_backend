package com.shoshin.plugins

import com.shoshin.common.ErrorInfo
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.notFound
import com.shoshin.common.exceptions.StatusException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<StatusException> { statusException ->
            println("statusException.message=${statusException.message}")
            call.respond(
                status = statusException.code,
                message = ErrorInfo(
                    code = statusException.code.value,
                    message = statusException.message ?: statusException.cause?.message ?: ""
                )
            )
        }
        exception<Throwable> { cause ->
            println("throwable.message=${cause.message}")
            call.internalServerError(throwable = cause)
        }
    }
}