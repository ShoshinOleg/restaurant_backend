package com.shoshin.routes.dishes

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.dishes.Dish
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.addDishRoute() {
    post("/categories/{id}/dishes") {
        println("POST: /categories/{id}/dishes")
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val categoryId = call.parameters["id"] ?: return@post call.badRequest()
        val dish = call.receive<Dish>()

        when(val isAdmin = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Error -> return@post call.internalServerError()
            is Reaction.Success -> {
                if(!isAdmin.data) {
                    return@post call.forbidden()
                } else {
                    if (dish.id == null) {
                        dish.id = DishesRepo.REF_DISHES.push().key ?: return@post call.internalServerError(
                            "Can't create new id for dish"
                        )
                    }
                    when(val result = DishesRepo.updateDish(dish, categoryId)) {
                        is Reaction.Success -> return@post call.ok(result.data)
                        is Reaction.Error -> return@post call.internalServerError(
                            result.exception.message ?: "Internal Server Error"
                        )
                    }
                }
            }
        }

    }
}

