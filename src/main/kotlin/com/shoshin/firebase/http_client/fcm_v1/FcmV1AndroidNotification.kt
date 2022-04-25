package com.shoshin.firebase.http_client.fcm_v1

import kotlinx.serialization.Serializable

@Serializable
data class FcmV1AndroidNotification(
    val click_action: String? = null
)