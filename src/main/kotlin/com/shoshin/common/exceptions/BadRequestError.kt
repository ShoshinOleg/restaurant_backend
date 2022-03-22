package com.shoshin.common.exceptions

import io.ktor.http.*

class BadRequestError(message: String? = "InternalServerError") :
    StatusException(
        message = message,
        code = HttpStatusCode.BadRequest
    )