package com.shoshin.common.exceptions

import io.ktor.http.*

open class StatusException(
    message: String?,
    open val code: HttpStatusCode = HttpStatusCode.InternalServerError,
    cause: Throwable? = null
) : Exception(message, cause)