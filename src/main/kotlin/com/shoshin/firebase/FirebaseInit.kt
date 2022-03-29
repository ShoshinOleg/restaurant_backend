package com.shoshin.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Bucket
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.StorageClient
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.shoshin.firebase.http_client.TestService
import com.shoshin.firebase.http_client.fcm.FcmConstants
import com.shoshin.firebase.http_client.fcm.FcmService
import com.shoshin.firebase.services.MessagingService
import io.ktor.client.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit

var firebaseStorage: Bucket? = null
var firebaseMessaging: FirebaseMessaging? = null
var messagingService: MessagingService? = null
var firebaseDatabase: FirebaseDatabase? = null
var httpClient: OkHttpClient? = null
var retrofit: Retrofit? = null
var fcmService: FcmService? = null
var FCM_API_KEY: String? = null

fun initFirebase() {
    val serviceAccount = getEnvServiceAccount() ?: getLocalServiceAccount()

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://restaurant-48d90-default-rtdb.europe-west1.firebasedatabase.app")
        .build()
    FirebaseApp.initializeApp(options)

    firebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance())
    firebaseStorage = StorageClient.getInstance(FirebaseApp.getInstance()).bucket("restaurant-48d90.appspot.com")
    firebaseMessaging = FirebaseMessaging.getInstance(FirebaseApp.getInstance())
    messagingService = MessagingService()

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    FCM_API_KEY = System.getenv("ADMIN_KEY")

    //https://catfact.ninja/fact

    retrofit = Retrofit.Builder()
        .baseUrl(FcmConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient!!)
        .build()
    fcmService = retrofit?.create(FcmService::class.java)
}

private fun getEnvServiceAccount() = System.getenv("ADMIN_KEY")?.let { ByteArrayInputStream(it.toByteArray()) }

private fun getLocalServiceAccount() = object {}.javaClass.getResourceAsStream("/restaurant-firebase-adminsdk.json")