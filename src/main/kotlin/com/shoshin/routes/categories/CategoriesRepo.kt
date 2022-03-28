package com.shoshin.routes.categories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shoshin.models.MenuCategory
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CategoriesRepo {
    companion object {
        fun newCategoryId(): String = REF_CATEGORIES.push().key

        suspend fun getCategories() : List<MenuCategory> {
            return suspendCoroutine { cont ->
                REF_CATEGORIES.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        val categories = mutableListOf<MenuCategory>()
                        if(snapshot != null) {
                            for(child in snapshot.children) {
                                categories.add(child.getValue(MenuCategory::class.java))
                            }
                        }
                        cont.resume(categories)
                    }

                    override fun onCancelled(error: DatabaseError?) {
                        throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    }
                })
            }
        }

        suspend fun getCategoryById(id: String): MenuCategory {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(id)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) =
                            if(snapshot != null)
                                continuation.resume(snapshot.getValue(MenuCategory::class.java))
                            else
                                throw NotFoundException("Category with id=$id not found")

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }
        }

        suspend fun setCategoryImage(categoryId: String, imageUrl: String) : Boolean {
            return suspendCoroutine { continuation ->
                REF_CATEGORIES
                    .child(categoryId)
                    .child("imageURL")
                    .setValue(imageUrl) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }

        suspend fun addCategory(category: MenuCategory): MenuCategory {
            return suspendCoroutine { cont ->
                REF_CATEGORIES.child(category.id)
                    .setValue(category
                    ) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            cont.resume(category)
                    }
            }
        }

        suspend fun removeCategory(categoryId: String): Boolean =
            suspendCoroutine { continuation ->
                REF_CATEGORIES.child(categoryId)
                    .removeValue { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
    }
}

