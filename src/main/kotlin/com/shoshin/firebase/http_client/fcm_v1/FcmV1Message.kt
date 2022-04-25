package com.shoshin.firebase.http_client.fcm_v1

import com.shoshin.firebase.http_client.fcm.FcmNotification
import kotlinx.serialization.Serializable

@Serializable
data class FcmV1Message (
    val token: String,
    val data: Map<String, String>? = null,
    val notification: FcmV1Notification? = null,
    val android: FcmV1Android? = null
)