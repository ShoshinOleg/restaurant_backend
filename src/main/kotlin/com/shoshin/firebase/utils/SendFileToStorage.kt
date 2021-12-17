package com.shoshin.firebase.utils

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.shoshin.firebase.firebaseStorage
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*

suspend fun ApplicationCall.sendFileToStorage(path: String, contentType: String): String {
    var targetUrl = ""
    receiveMultipart().forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val fileBytes = part.streamProvider().readBytes()
                val blob = firebaseStorage?.create(
                    path,
                    fileBytes,
                    contentType,
                    Bucket.BlobTargetOption.predefinedAcl(
                        Storage.PredefinedAcl.PUBLIC_READ
                    )
                )
                val encodedBlobName = java.net.URLEncoder.encode(blob?.name, "utf-8")
                targetUrl = "https://firebasestorage.googleapis.com/v0/b/${blob?.bucket}/o/$encodedBlobName?alt=media"
                println("targetUrl=$targetUrl")
            }
            else -> {}
        }
    }
    return targetUrl
}