package com.shoshin.firebase.http_client

import retrofit2.http.GET

interface TestService {
    @GET(Constants.testUrl)
    suspend fun getTest(): TestObject
}