package com.shoshin.routes.users

import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.registerSignInUserRoute() {
    post("/users/register") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        when(val result = UsersRepo.onSignUser(principal)) {
            is Reaction.Success -> return@post call.ok("User signed registered")
            is Reaction.Error -> return@post call.internalServerError(
                result.exception.message ?: "Internal Server Error"
            )
        }
    }
}