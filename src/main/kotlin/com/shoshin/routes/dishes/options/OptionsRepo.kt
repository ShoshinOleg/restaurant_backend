package com.shoshin.routes.dishes.options

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shoshin.models.dishes.DishOption
import com.shoshin.routes.dishes.DishesRepo
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OptionsRepo {
    companion object {
        private fun newOptionId(dishId: String) = DishesRepo.REF_DISHES.child(dishId).child("options").push().key

        suspend fun getOption(dishId: String, optionId: String): DishOption {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(optionId)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot != null) {
                                val option = snapshot.getValue(DishOption::class.java)
                                continuation.resume(option)
                            } else {
                                throw NotFoundException("Dish option with id=$optionId not found")
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }
        }

        suspend fun addOption(dishId: String, option: DishOption) : Boolean {
            return suspendCoroutine { continuation ->
                option.id = option.id ?: newOptionId(dishId)
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(option.id)
                    .setValue(option) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }

        suspend fun removeOption(dishId: String, optionId: String) : Boolean {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(optionId)
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