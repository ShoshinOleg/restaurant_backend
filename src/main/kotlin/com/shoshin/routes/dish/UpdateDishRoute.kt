package com.shoshin.routes.dish

import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.models.dishes.Dish
import com.shoshin.routes.REF_CATEGORIES
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Route.updateDishRoute() {
    post("/categories/{id}/dishes") {

        println("POST: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@post call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(ApiError(message = "BadRequest"))
        )
        println("GET: /categories/{id}/dishes - :: categoryId=$categoryId")
        val dish = call.receive<Dish>()
        if (dish.id == null) {
            dish.id = REF_DISHES.push().key ?: return@post call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    ApiError(message = "Can't create new id for dish")
                )
            )
        }
        println("dish=$dish")
        when(val result = updateDish(dish, categoryId)) {
            is Reaction.Success -> {
                return@post call.respond(
                    status = HttpStatusCode.OK,
                    message = result.data
                )
            }
            is Reaction.Error -> {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    ErrorResponse(
                        ApiError(
                            message = result.exception.message
                        )
                    )
                )
            }
        }

    }
}

suspend fun updateDish(dish: Dish, categoryId: String): Reaction<Dish> {
    when(val addDishResult = addDish(dish)) {
        is Reaction.Success -> {
            when(val addToCatResult = addDishToCategory(dish,categoryId)) {
                is Reaction.Success -> {
                    return addToCatResult
                }
                is Reaction.Error -> {
                    return addToCatResult
                }
            }
        }
        is Reaction.Error -> {
            return addDishResult
        }
    }
}

suspend fun addDishToCategory(dish: Dish, categoryId: String): Reaction<Dish> {
    return suspendCoroutine { continuation ->
        REF_CATEGORIES
            .child(categoryId)
            .child("dishesIds")
            .child(dish.id)
            .setValue(dish.id) { error, ref ->
                if(error != null ) {
                    continuation.resume(
                        Reaction.Error(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.Success(dish)
                    )
                }
            }
    }
}

suspend fun addDish(dish: Dish): Reaction<Dish> {
    return suspendCoroutine { continuation ->
        REF_DISHES
            .child(dish.id)
            .setValue(dish) { error, ref ->
                if(error != null ) {
                    continuation.resume(
                        Reaction.Error(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.Success(dish)
                    )
                }
            }
    }
}