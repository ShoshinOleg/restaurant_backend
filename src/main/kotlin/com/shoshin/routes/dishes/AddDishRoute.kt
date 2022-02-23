package com.shoshin.routes.dishes

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

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            dish.id = dish.id ?: DishesRepo.newDishId()
            DishesRepo.updateDish(dish, categoryId)
            return@post call.ok(dish)
        }
    }
}

