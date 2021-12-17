package com.shoshin.routes.dish

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.firebaseStorage
import com.shoshin.firebase.utils.sendFileToStorage
import com.shoshin.routes.REF_CATEGORIES
import com.shoshin.routes.checkRole
import com.shoshin.routes.setCategoryImage
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Route.setImageForDishRoute() {
    post("/dishes/{dishId}") {
        val dishId = call.parameters["dishId"] ?:  return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()

        when(val checkRoleRes = checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(checkRoleRes.data) {
                    val targetUrl = call.sendFileToStorage(
                        "images/menu/dishes/$dishId",
                        "image/jpeg"
                    )
                    when(setDishImage(dishId, targetUrl)) {
                        is Reaction.Success -> return@post call.ok(targetUrl)
                        is Reaction.Error -> return@post call.internalServerError()
                    }
                } else return@post call.forbidden()
            }
            is Reaction.Error -> return@post call.internalServerError()
        }
    }
}

suspend fun setDishImage(dishId: String, url: String): Reaction<Boolean> {
    return suspendCoroutine { continuation ->
        REF_DISHES
            .child(dishId)
            .child("imageURL")
            .setValue(url) { error, _ ->
                if(error != null) {
                    continuation.resume(
                        Reaction.Error(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.Success(true)
                    )
                }
            }
    }
}