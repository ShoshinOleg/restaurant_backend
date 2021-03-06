package com.shoshin.routes.users

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.registerSignInUserRoute() {
    post("/users/register") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        UsersRepo.onSignUser(principal)
        return@post call.ok("User signed registered")
    }
}