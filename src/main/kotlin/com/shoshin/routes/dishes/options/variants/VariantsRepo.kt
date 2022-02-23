package com.shoshin.routes.dishes.options.variants

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.shoshin.models.dishes.DishOptionVariant
import com.shoshin.routes.dishes.DishesRepo
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VariantsRepo {
    companion object {
        private fun variantsRef(dishId: String, optionId: String): DatabaseReference =
            DishesRepo.REF_DISHES
                .child(dishId)
                .child("options")
                .child(optionId)
                .child("variants")

        private fun newVariantId(dishId: String, optionId: String) : String =
            variantsRef(dishId, optionId).push().key

        suspend fun getVariant(dishId: String, optionId: String, variantId: String): DishOptionVariant =
            suspendCoroutine { continuation ->
                variantsRef(dishId, optionId).child(variantId)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot != null) {
                                val option = snapshot.getValue(DishOptionVariant::class.java)
                                continuation.resume(option)
                            } else {
                                throw NotFoundException("Option variant with id=$variantId not found")
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }

        suspend fun addVariant(dishId: String, optionId: String, variant: DishOptionVariant) : Boolean =
            suspendCoroutine { continuation ->
                variant.id = variant.id ?: newVariantId(dishId, optionId)
                variantsRef(dishId, optionId).child(variant.id)
                    .setValue(variant) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }

        suspend fun removeVariant(dishId: String, optionId: String, variantId: String) : Boolean =
            suspendCoroutine { continuation ->
                variantsRef(dishId, optionId).child(variantId)
                    .removeValue { error, ref ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
    }
}
