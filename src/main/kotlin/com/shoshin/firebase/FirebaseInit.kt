package com.shoshin.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.storage.Bucket
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import java.io.ByteArrayInputStream

var firebaseStorage: Bucket? = null

fun initFirebase() {
    val serviceAccount = getEnvServiceAccount() ?: getLocalServiceAccount()

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://restaurant-48d90-default-rtdb.europe-west1.firebasedatabase.app")
        .build()
    FirebaseApp.initializeApp(options)

    firebaseStorage = StorageClient.getInstance().bucket()
}

private fun getEnvServiceAccount() = System.getenv("ADMIN_KEY")?.let { ByteArrayInputStream(it.toByteArray()) }

private fun getLocalServiceAccount() = object {}.javaClass.getResourceAsStream("/restaurant-firebase-adminsdk.json")