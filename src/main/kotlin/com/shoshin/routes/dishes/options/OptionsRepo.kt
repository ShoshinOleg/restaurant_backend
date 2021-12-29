package com.shoshin.routes.dishes.options

import com.shoshin.common.Reaction
import com.shoshin.models.dishes.DishOption
import com.shoshin.routes.dishes.DishesRepo
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OptionsRepo {
    companion object {
        private fun newOptionId(dishId: String) = DishesRepo.REF_DISHES.child(dishId).child("options").key

        suspend fun addOption(dishId: String, option: DishOption) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                if(option.id == null) {
                    option.id = newOptionId(dishId)
                }
                println("optionId=${option.id}")
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(option.id)
                    .setValue(option) { error, _ ->
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

        suspend fun removeOption(dishId: String, optionId: String) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(optionId)
                    .removeValue { error, _ ->
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