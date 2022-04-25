package com.shoshin.firebase.http_client.fcm_v1

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FcmV1Service {
    @POST(FcmV1Constants.SEND_MESSAGE)
    suspend fun sendMessage(
        @Header("Authorization") authString: String,
        @Body message: FcmV1Wrapper
    )
}