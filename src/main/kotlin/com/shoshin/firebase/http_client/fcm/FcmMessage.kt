package com.shoshin.firebase.http_client.fcm

import kotlinx.serialization.Serializable

@Serializable
data class FcmMessage(
    val to: String,
    val notification: FcmNotification? = null
)