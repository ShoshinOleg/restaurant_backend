package com.shoshin.plugins

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.notFound
import io.ktor.application.*
import io.ktor.features.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.internalServerError()
        }
        exception<NotFoundException> { cause ->
            call.notFound(cause.message)
        }
    }
}