package com.shoshin.routes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.ApiError
import com.shoshin.common.ApiResponse
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.domain_abstract.entities.dish.Dish
import io.ktor.application.*
import io.ktor.http.*
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
    }
}

fun Route.dishesRoutes() {
    get("/categories/{id}/dishes") {
        println("GET: /categories/{id}/dishes")
        val categoryId = call.parameters["id"] ?: return@get call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(ApiError(message = "BadRequest"))
        )
        when(val dishesIdsResult = getDishesIdsForCategory(categoryId)) {
            is Reaction.OnSuccess -> {
                when(val dishesResult = getDishes(dishesIdsResult.data)) {
                    is Reaction.OnSuccess -> {
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
            .child("itemsIds")
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
//        when(val resultDishesIds = getDishesIdsForCategory()) {
//
//        }
    }
}