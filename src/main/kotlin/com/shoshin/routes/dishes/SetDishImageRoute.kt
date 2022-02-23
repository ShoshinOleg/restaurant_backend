package com.shoshin.routes.dishes

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.utils.sendFileToStorage
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.setImageForDishRoute() {
    post("/dishes/{dishId}") {
        val dishId = call.parameters["dishId"] ?:  return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            val targetUrl = call.sendFileToStorage(
                "images/menu/dishes/$dishId",
                "image/jpeg"
            )
            DishesRepo.setDishImage(dishId, targetUrl)
            return@post call.ok(targetUrl)
        }
    }
}

