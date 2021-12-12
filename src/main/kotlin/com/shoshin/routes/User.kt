package com.shoshin.routes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val REF_USERS : DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("users")

val REF_ROLES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("roles")

fun Application.registerUserRoutes() {
    routing {
        authenticate("firebase") {
            registerSignInUserRoute()
            hasRoleRoute()
        }
    }
}

fun Route.hasRoleRoute() {
    get("users/hasRole") {
        val role = call.request.queryParameters["role"] ?: return@get call.respond(
            status = HttpStatusCode.BadRequest,
            message = "No set role name"
        )
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.respond(
            status = HttpStatusCode.InternalServerError,
            ErrorResponse(ApiError(message = "InternalServerError"))
        )
        when(val result = checkRole(principal, role)) {
            is Reaction.Success -> {
                call.respond(
                    HttpStatusCode.OK,
                    result.data
                )
            }
            is Reaction.Error -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "InternalServerError"
                )
            }
        }

    }
}

fun Route.registerSignInUserRoute() {
    post("/users/register") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.respond(
            status = HttpStatusCode.InternalServerError,
            ErrorResponse(ApiError(message = "InternalServerError"))
        )

        when(val result = onSignUser(principal)) {
            is Reaction.Success -> {
                return@post call.respond(
                    status = HttpStatusCode.OK,
                    message = "User signed registered"
                )
            }
            is Reaction.Error -> {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    ErrorResponse(
                        ApiError(
                            message = result.exception.message
                        )
                    )
                )
            }
        }
    }
}

suspend fun onSignUser(principal: FirebasePrincipal): Reaction<Unit> {
    return suspendCoroutine { continuation ->
        REF_USERS.child(principal.userId)
            .child("id")
            .setValue(principal.userId) { error, ref ->
                if(error != null ) {
                    continuation.resume(
                        Reaction.Error(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.Success(Unit)
                    )
                }
            }
    }
}

suspend fun checkRole(principal: FirebasePrincipal, role: String): Reaction<Boolean> {
    return suspendCoroutine {  cont ->
        REF_ROLES.child(role)
            .child(principal.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    val isAdmin = snapshot?.getValue(Boolean::class.java)
                    cont.resume(
                        Reaction.Success(isAdmin!!)
                    )
                }

                override fun onCancelled(error: DatabaseError?) {
                    cont.resume(
                        Reaction.Success(false)
                    )
                }
            })
    }
}















