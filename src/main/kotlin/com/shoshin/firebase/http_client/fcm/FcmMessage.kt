package com.shoshin.firebase.http_client.fcm

import kotlinx.serialization.Serializable

@Serializable
data class FcmMessage(
    val to: String,
    val data: Map<String, String>? = null,
    val notification: FcmNotification? = null,
)