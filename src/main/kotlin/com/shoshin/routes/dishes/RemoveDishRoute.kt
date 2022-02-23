package com.shoshin.routes.dishes

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeDishRoute() {
    delete("categories/{categoryId}/dishes/{dishId}") {
        val categoryId = call.parameters["categoryId"] ?: return@delete call.badRequest()
        val dishId = call.parameters["dishId"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@delete call.forbidden()
        } else {
            val dish = DishesRepo.getDish(dishId)
            DishesRepo.removeDishFromCategory(categoryId, dishId)
            DishesRepo.removeDish(dishId)
            return@delete call.ok(dish)
        }
    }
}