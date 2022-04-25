package com.shoshin.firebase.http_client.fcm_v1

import kotlinx.serialization.Serializable

@Serializable
data class FcmV1Wrapper (
    val message: FcmV1Message
)