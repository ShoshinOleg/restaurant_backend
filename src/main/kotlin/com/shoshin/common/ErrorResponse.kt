package com.shoshin.common

class ErrorResponse (
    val errors: List<ApiError>? = null
) {
    constructor(vararg errors: ApiError)
        : this(errors.toList())
}