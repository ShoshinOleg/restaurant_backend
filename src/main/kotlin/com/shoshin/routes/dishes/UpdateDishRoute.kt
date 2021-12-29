package com.shoshin.routes.dishes

import com.shoshin.common.Reaction
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

fun Route.updateDishRoute() {
    put("/categories/{id}/dishes") {
        val dish = call.receive<Dish>()
        val principal = call.principal<FirebasePrincipal>() ?: return@put call.internalServerError()

        when(val checkRoleRes = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(!checkRoleRes.data) {
                    return@put call.forbidden()
                } else {
                    if (dish.id == null) {
                        dish.id = DishesRepo.REF_DISHES.push().key ?: return@put call.internalServerError(
                            "Can't create new id for dish"
                        )
                    }
                    when(val result = DishesRepo.updateDish(dish)) {
                        is Reaction.Success -> return@put call.ok(result.data)
                        is Reaction.Error -> return@put call.internalServerError(
                            result.exception.message ?: "Internal Server Error"
                        )
                    }
                }
            }
            is Reaction.Error -> return@put call.internalServerError()
        }
    }
}