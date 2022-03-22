package com.shoshin.common

import kotlinx.serialization.Serializable

@Serializable
class ErrorInfo(
    val code: Int,
    val message: String
)