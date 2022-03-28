package com.shoshin.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Bucket
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.StorageClient
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.shoshin.firebase.services.MessagingService
import java.io.ByteArrayInputStream

var firebaseStorage: Bucket? = null
var firebaseMessaging: FirebaseMessaging? = null
var messagingService: MessagingService? = null
var firebaseDatabase: FirebaseDatabase? = null


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
}

private fun getEnvServiceAccount() = System.getenv("ADMIN_KEY")?.let { ByteArrayInputStream(it.toByteArray()) }

private fun getLocalServiceAccount() = object {}.javaClass.getResourceAsStream("/restaurant-firebase-adminsdk.json")