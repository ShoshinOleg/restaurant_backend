package com.shoshin.routes.dishes

import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.models.dishes.Dish
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.updateDishRoute() {
    post("/categories/{id}/dishes") {
        println("POST: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@post call.badRequest()
        val dish = call.receive<Dish>()
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

