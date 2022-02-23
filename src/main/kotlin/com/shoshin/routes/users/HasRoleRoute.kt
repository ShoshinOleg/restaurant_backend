package com.shoshin.routes.users

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.hasRoleRoute() {
    get("users/hasRole") {
        val role = call.request.queryParameters["role"] ?: return@get call.badRequest("No set role name")
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.internalServerError()
        val hasRole = UsersRepo.checkRole(principal, role)
        return@get call.ok(hasRole)
    }
}