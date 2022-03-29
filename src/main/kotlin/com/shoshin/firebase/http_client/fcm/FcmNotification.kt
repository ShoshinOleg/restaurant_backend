package com.shoshin.firebase.http_client.fcm

import kotlinx.serialization.Serializable

@Serializable
data class FcmNotification(
    val title: String? = null,
    val body: String? = null
)