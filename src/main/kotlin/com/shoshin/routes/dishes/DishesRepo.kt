package com.shoshin.routes.dishes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.Reaction
import com.shoshin.models.dishes.Dish
import com.shoshin.routes.categories.REF_CATEGORIES
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DishesRepo {
    companion object {
        val REF_DISHES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("menu")
            .child("dishes")

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

        suspend fun setDishImage(dishId: String, url: String): Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("imageURL")
                    .setValue(url) { error, _ ->
                        if(error != null) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(true)
                            )
                        }
                    }
            }
        }

        suspend fun updateDish(dish: Dish, categoryId: String? = null): Reaction<Dish> {
            when(val addDishResult = addDish(dish)) {
                is Reaction.Success -> {
                    if(categoryId != null) {
                        when(val addToCatResult = addDishToCategory(dish,categoryId)) {
                            is Reaction.Success -> {
                                return addToCatResult
                            }
                            is Reaction.Error -> {
                                return addToCatResult
                            }
                        }
                    } else {
                        return addDishResult
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

        suspend fun removeDishFromCategory(categoryId: String, dishId: String) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
                    .child("dishesIds")
                    .child(dishId)
                    .removeValue { error, ref ->
                        if(error != null) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(true)
                            )
                        }
                    }
            }
        }

        suspend fun removeDish(dishId: String) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                REF_DISHES
                    .child(dishId)
                    .removeValue { error, ref ->
                        if(error != null) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(true)
                            )
                        }
                    }
            }
        }
    }
}