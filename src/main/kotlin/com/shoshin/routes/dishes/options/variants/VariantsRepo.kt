package com.shoshin.routes.dishes.options.variants

import com.shoshin.common.Reaction
import com.shoshin.models.dishes.DishOptionVariant
import com.shoshin.routes.dishes.DishesRepo
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VariantsRepo {
    companion object {
        private fun newVariantId(
            dishId: String,
            optionId: String,
        ) : String = DishesRepo.REF_DISHES
            .child(dishId)
            .child("options")
            .child(optionId)
            .child("variants")
            .key

        suspend fun addVariant(
            dishId: String,
            optionId: String,
            variant: DishOptionVariant
        ) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                if(variant.id == null) {
                    variant.id = newVariantId(dishId, optionId)
                }
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(optionId)
                    .child("variants")
                    .child(variant.id)
                    .setValue(variant) { error, _ ->
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

        suspend fun removeVariant(
            dishId: String,
            optionId: String,
            variantId: String
        ) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                DishesRepo.REF_DISHES
                    .child(dishId)
                    .child("options")
                    .child(optionId)
                    .child("variants")
                    .child(variantId)
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
