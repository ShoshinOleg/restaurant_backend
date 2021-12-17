package com.shoshin.routes.dish

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.models.dishes.Dish
import com.shoshin.routes.REF_CATEGORIES
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Route.dishesRoute() {
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
            is Reaction.Success -> {
                when(val dishesResult = getDishes(dishesIdsResult.data)) {
                    is Reaction.Success -> {
                        println("GET: /categories/{id}/dishes - ::dishesIdsResult = $dishesIdsResult ")

                        return@get call.respond(
                            status = HttpStatusCode.OK,
                            dishesResult.data
                        )
                    }
                    is Reaction.Error -> {
                        return@get call.respond(
                            status = HttpStatusCode.InternalServerError,
                            ErrorResponse(
                                ApiError(message = "InternalError")
                            )
                        )
                    }
                }
            }
            is Reaction.Error -> {
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
                        continuation.resume(Reaction.Success(mutableDishesIds))
                    } else {
                        continuation.resume(Reaction.Error(Throwable("Not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getDishesIdsForCategory = ${error?.message}")
                    continuation.resume(
                        Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
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
                        continuation.resume(Reaction.Success(dishes))
                    } else {
                        continuation.resume(Reaction.Error(Throwable("Not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getDishes = ${error?.message}")
                    continuation.resume(
                        Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
                    )
                }

            })
    }
}