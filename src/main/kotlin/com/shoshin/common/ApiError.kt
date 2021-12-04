package com.shoshin.common

data class ApiError(
    val code: Int? = null,
    val message: String? = null,
    val link: String? = null
)