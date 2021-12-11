package com.shoshin.routes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.models.dish.Dish
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val REF_DISHES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("menu")
    .child("dishes")

fun Application.registerDishesRoutes() {
    routing {
        dishesRoutes()

        updateDishRoute()
    }
}

fun Route.dishesRoutes() {
    get("/categories/{id}/dishes") {
//        FirebaseApp.getInstance()
//        call.principal<>()
        println("GET: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@get call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(ApiError(message = "BadRequest"))
        )
        println("GET: /categories/{id}/dishes - :: categoryId=$categoryId")
        when(val dishesIdsResult = getDishesIdsForCategory(categoryId)) {
            is Reaction.OnSuccess -> {
                when(val dishesResult = getDishes(dishesIdsResult.data)) {
                    is Reaction.OnSuccess -> {
                        println("GET: /categories/{id}/dishes - ::dishesIdsResult = $dishesIdsResult ")

                        return@get call.respond(
                            status = HttpStatusCode.OK,
                            dishesResult.data
                        )
                    }
                    is Reaction.OnError -> {
                        return@get call.respond(
                            status = HttpStatusCode.InternalServerError,
                            ErrorResponse(
                                ApiError(message = "InternalError")
                            )
                        )
                    }
                }
            }
            is Reaction.OnError -> {
                return@get call.respond(
                    status = HttpStatusCode.InternalServerError,
                    ErrorResponse(
                        ApiError(message = "InternalError")
                    )
                )
            }
        }
    }
}



suspend fun getDishesIdsForCategory(categoryId: String): Reaction<List<String>> {
    return suspendCoroutine { continuation ->
        REF_CATEGORIES
            .child(categoryId)
            .child("dishesIds")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    if(snapshot != null) {
                        val mutableDishesIds = mutableListOf<String>()
                        for(child in snapshot.children) {
                            mutableDishesIds.add(
                                child.getValue(String::class.java)
                            )
                        }
                        continuation.resume(Reaction.OnSuccess(mutableDishesIds))
                    } else {
                        continuation.resume(Reaction.OnError(Throwable("Not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getDishesIdsForCategory = ${error?.message}")
                    continuation.resume(
                        Reaction.OnError(Throwable(error?.message, error?.toException()?.cause))
                    )
                }
            })
    }
}

suspend fun getDishes(dishIds: List<String>): Reaction<List<Dish>> {
    return suspendCoroutine { continuation ->
        REF_DISHES
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    if(snapshot != null) {
                        val dishes = mutableListOf<Dish>()
                        for(child in snapshot.children) {
                            val dish = child.getValue(Dish::class.java)
                            if(dishIds.contains(dish.id)) {
                                dishes.add(dish)
                            }
                        }
                        continuation.resume(Reaction.OnSuccess(dishes))
                    } else {
                        continuation.resume(Reaction.OnError(Throwable("Not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getDishes = ${error?.message}")
                    continuation.resume(
                        Reaction.OnError(Throwable(error?.message, error?.toException()?.cause))
                    )
                }

            })
    }
}

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
            is Reaction.OnSuccess -> {
                return@post call.respond(
                    status = HttpStatusCode.OK,
                    message = result.data
                )
            }
            is Reaction.OnError -> {
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
        is Reaction.OnSuccess -> {
            when(val addToCatResult = addDishToCategory(dish,categoryId)) {
                is Reaction.OnSuccess -> {
                    return addToCatResult
                }
                is Reaction.OnError -> {
                    return addToCatResult
                }
            }
        }
        is Reaction.OnError -> {
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
                        Reaction.OnError(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.OnSuccess(dish)
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
                        Reaction.OnError(error.toException())
                    )
                } else {
                    continuation.resume(
                        Reaction.OnSuccess(dish)
                    )
                }
            }
    }
}