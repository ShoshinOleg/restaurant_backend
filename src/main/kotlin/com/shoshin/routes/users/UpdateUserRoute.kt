package com.shoshin.routes.users

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.users.RestaurantUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.updateUserRoute() {
    post("/users") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val restaurantUser = call.receive<RestaurantUser>()
        UsersRepo.updateUser(principal, restaurantUser)
        return@post call.ok()
    }
}