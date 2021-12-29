package com.shoshin.routes.dishes

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getDishesRoute() {
    get("/categories/{id}/dishes") {
        println("GET: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@get call.badRequest()
        when(val dishesIdsResult = DishesRepo.getDishesIdsForCategory(categoryId)) {
            is Reaction.Success -> {
                when(val dishesResult = DishesRepo.getDishes(dishesIdsResult.data)) {
                    is Reaction.Success -> return@get call.ok(dishesResult.data)
                    is Reaction.Error -> return@get call.internalServerError()
                }
            }
            is Reaction.Error -> return@get call.internalServerError()
        }
    }
}

