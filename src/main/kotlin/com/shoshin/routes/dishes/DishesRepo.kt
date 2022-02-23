package com.shoshin.routes.dishes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.models.dishes.Dish
import com.shoshin.routes.categories.REF_CATEGORIES
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DishesRepo {
    companion object {
        val REF_DISHES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("menu")
            .child("dishes")

        fun newDishId(): String = REF_DISHES.push().key

        suspend fun getDish(dishId: String): Dish =
            suspendCoroutine { continuation ->
                REF_DISHES.child(dishId)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot != null) {
                                val dish = snapshot.getValue(Dish::class.java)
                                continuation.resume(dish)
                            } else {
                                throw NotFoundException("Dish with id=$dishId not found")
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }

        suspend fun getDishesIdsForCategory(categoryId: String): List<String> {
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
                                continuation.resume(mutableDishesIds)
                            } else
                                throw NotFoundException()
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }
        }

        suspend fun getDishes(dishIds: List<String>): List<Dish> {
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
                                continuation.resume(dishes)
                            } else
                                throw NotFoundException()
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }
        }

        suspend fun setDishImage(dishId: String, url: String): Boolean {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("imageURL")
                    .setValue(url) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }

        suspend fun updateDish(dish: Dish, categoryId: String? = null) {
            addDish(dish)
            if(categoryId != null) {
                addDishToCategory(dish,categoryId)
            }
        }

        suspend fun addDishToCategory(dish: Dish, categoryId: String): Dish {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
                    .child("dishesIds")
                    .child(dish.id)
                    .setValue(dish.id) { error, ref ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(dish)
                    }
            }
        }

        suspend fun addDish(dish: Dish): Dish {
            return suspendCoroutine { continuation ->
                REF_DISHES
                    .child(dish.id)
                    .setValue(dish) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(dish)
                    }
            }
        }

        suspend fun removeDishFromCategory(categoryId: String, dishId: String) : Boolean {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
                    .child("dishesIds")
                    .child(dishId)
                    .removeValue { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }

        suspend fun removeDish(dishId: String) : Boolean {
            return suspendCoroutine { continuation ->
                REF_DISHES
                    .child(dishId)
                    .removeValue { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }
    }
}