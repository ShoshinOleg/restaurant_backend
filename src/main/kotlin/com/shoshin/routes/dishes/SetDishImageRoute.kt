package com.shoshin.routes.dishes

import com.shoshin.common.Reaction
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

        when(val checkRoleRes = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(checkRoleRes.data) {
                    val targetUrl = call.sendFileToStorage(
                        "images/menu/dishes/$dishId",
                        "image/jpeg"
                    )
                    when(DishesRepo.setDishImage(dishId, targetUrl)) {
                        is Reaction.Success -> return@post call.ok(targetUrl)
                        is Reaction.Error -> return@post call.internalServerError()
                    }
                } else return@post call.forbidden()
            }
            is Reaction.Error -> return@post call.internalServerError()
        }
    }
}

