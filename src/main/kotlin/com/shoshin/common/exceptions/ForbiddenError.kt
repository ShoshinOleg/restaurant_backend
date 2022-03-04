package com.shoshin.common.exceptions

import io.ktor.http.*

class ForbiddenError(message: String? = "Forbidden error") :
    StatusException(
        message = message,
        code = HttpStatusCode.Forbidden.value
    )