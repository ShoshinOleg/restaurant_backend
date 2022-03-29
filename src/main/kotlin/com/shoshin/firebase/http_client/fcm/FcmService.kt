package com.shoshin.firebase.http_client.fcm

import com.google.firebase.messaging.Message
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FcmService {
    @POST(FcmConstants.SEND_MESSAGES)
    suspend fun sendMessage(
        @Header("Authorization") authString: String,
        @Body message: Message
    )
}