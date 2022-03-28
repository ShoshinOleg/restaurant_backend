package com.shoshin.firebase.http_client

import kotlinx.serialization.Serializable

@Serializable
data class TestObject(
    val fact: String,
    val length: Int
)

//{"fact":"The first cat show was in 1871 at the Crystal Palace in London.","length":63}