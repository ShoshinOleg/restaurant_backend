package com.shoshin.routes.dishes

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
    put("/dishes/{id}") {
        val dish = call.receive<Dish>()
        val principal = call.principal<FirebasePrincipal>() ?: return@put call.internalServerError()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@put call.forbidden()
        } else {
            dish.id = dish.id ?: DishesRepo.newDishId()
            DishesRepo.updateDish(dish)
            return@put call.ok(dish)
        }
    }
}