package com.shoshin.common.exceptions

import io.ktor.http.*

open class StatusException(
    message: String?,
    open val code: Int = HttpStatusCode.InternalServerError.value,
    cause: Throwable? = null
) : Exception(message, cause)