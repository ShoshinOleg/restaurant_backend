package com.shoshin.routes.dishes

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getDishesRoute() {
    get("/categories/{id}/dishes") {
        println("GET: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@get call.badRequest()
        val dishesIds = DishesRepo.getDishesIdsForCategory(categoryId)
        val dishes = DishesRepo.getDishes(dishesIds)
        return@get call.ok(dishes)
    }
}

