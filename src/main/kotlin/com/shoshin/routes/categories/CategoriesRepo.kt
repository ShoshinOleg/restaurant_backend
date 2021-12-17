package com.shoshin.routes.categories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shoshin.common.Reaction
import com.shoshin.models.MenuCategory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CategoriesRepo {
    companion object {
        suspend fun getCategories() : Reaction<List<MenuCategory>> {
            return suspendCoroutine { cont ->
                REF_CATEGORIES
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val categories = mutableListOf<MenuCategory>()
                            if(snapshot != null) {
                                println("snapshot = $snapshot")
                                for(child in snapshot.children) {
                                    categories.add(child.getValue(MenuCategory::class.java))
                                }
                            }
                            cont.resume(Reaction.Success(categories))
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            println("getCategoriesError = ${error?.message}")
                            cont.resume(Reaction.Error(Throwable(error?.message, error?.toException()?.cause)))
                        }
                    })
            }
        }

        suspend fun getCategoryById(id: String): Reaction<MenuCategory> {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(id)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot != null) {
                                println("snapshot=$snapshot")
                                val category = snapshot.getValue(MenuCategory::class.java)
                                println("category=$category")
                                continuation.resume(Reaction.Success(category))
                            } else {
                                continuation.resume(Reaction.Error(Throwable("Not found")))
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            println("getCategoryError = ${error?.message}")
                            continuation.resume(
                                Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
                            )
                        }
                    })
            }
        }

        suspend fun setCategoryImage(categoryId: String, imageUrl: String) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
                    .child("imageURL")
                    .setValue(imageUrl) { error, _ ->
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

        suspend fun addCategory(category: MenuCategory): Reaction<MenuCategory> {
            return suspendCoroutine { cont ->
                REF_CATEGORIES.child("${category.id}")
                    .setValue(category
                    ) { error, ref ->
                        if(error != null ) {
                            cont.resume(Reaction.Error(
                                error.toException()
                            ))
                        } else {
                            cont.resume(Reaction.Success(
                                category
                            ))
                        }
                    }
            }
        }

        suspend fun removeCategory(categoryId: String): Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
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

