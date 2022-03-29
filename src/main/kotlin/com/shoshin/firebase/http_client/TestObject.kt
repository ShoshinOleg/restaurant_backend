package com.shoshin.firebase.http_client

import kotlinx.serialization.Serializable

@Serializable
data class TestObject(
    val fact: String,
    val length: Int
)

