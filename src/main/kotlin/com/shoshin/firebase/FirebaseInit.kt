package com.shoshin.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.ByteArrayInputStream

fun initFirebase() {
    val serviceAccount = getEnvServiceAccount() ?: getLocalServiceAccount()

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://restaurant-48d90-default-rtdb.europe-west1.firebasedatabase.app")
        .build()

    FirebaseApp.initializeApp(options)
}

private fun getEnvServiceAccount() = System.getenv("ADMIN_KEY")?.let { ByteArrayInputStream(it.toByteArray()) }

private fun getLocalServiceAccount() = object {}.javaClass.getResourceAsStream("/restaurant-firebase-adminsdk.json")