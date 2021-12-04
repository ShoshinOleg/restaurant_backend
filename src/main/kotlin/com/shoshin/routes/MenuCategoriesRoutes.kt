package com.shoshin.routes

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.ApiError
import com.shoshin.common.ApiResponse
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.models.MenuCategory
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


val REF_CATEGORIES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("menu")
    .child("categories")

fun Application.registerMenuCategoriesRoutes() {
    routing {
        menuCategoriesRoute()
    }
}

fun Route.menuCategoriesRoute() {
//    get("/category") {
////        FirebaseApp.getInstance()
//
//    }

    post("/category") {
        println("/category")
        val category = call.receive<MenuCategory>()
        if(category.id == null) {
            category.id = REF_CATEGORIES.push().key
            category.id ?: return@post call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    ApiError(message = "Can't create new id for category")
                )
            )
        }
        println("category=$category")
        if(addCategory(category)) {
            println("category added")
            return@post call.respond(
                status = HttpStatusCode.Created,
                category.id!!
            )
        } else {
            println("category not added")
            return@post call.respond(
                status = HttpStatusCode.InternalServerError,
                ErrorResponse(
                    ApiError(message = "Category not added")
                )
            )
        }
    }

    get("/category") {
        println("GET: /category")
        when(val result = getCategories()) {
            is Reaction.OnSuccess -> {
                return@get call.respond(
                    status = HttpStatusCode.OK,
                    result.data
                )
            }
            is Reaction.OnError -> {
                return@get call.respond(
                    status = HttpStatusCode.NotFound,
                    ErrorResponse(
                        ApiError(message = "asd")
                    )
                )
            }
        }
    }

    get("/category/{id}") {
        val id = call.parameters["id"] ?: return@get call.respond(
            status = HttpStatusCode.BadRequest,
            ApiResponse.failure(null)
        )
        when(val result = getCategoryById(id)) {
            is Reaction.OnSuccess -> {
                return@get call.respond(
                    status = HttpStatusCode.OK,
                    ApiResponse.success(result.data)
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
}

suspend fun addCategory(category: MenuCategory): Boolean {
    return suspendCoroutine { cont ->
        REF_CATEGORIES.child("${category.id}")
            .setValue(category
            ) { error, ref ->
                if(error != null ) {
                    cont.resume(false)
                } else {
                    cont.resume(true)
                }
            }
    }
}

suspend fun getCategories() : Reaction<List<MenuCategory>> {
    return suspendCoroutine { cont ->
        REF_CATEGORIES
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    val categories = mutableListOf<MenuCategory>()
                    if(snapshot != null) {
                        for(child in snapshot.children) {
                            categories.add(child.getValue(MenuCategory::class.java))
                        }
                    }
                    cont.resume(Reaction.OnSuccess(categories))
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getCategoriesError = ${error?.message}")

                    cont.resume(Reaction.OnError(Throwable(error?.message, error?.toException()?.cause)))
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
                        val category = snapshot.getValue(MenuCategory::class.java)
                        continuation.resume(Reaction.OnSuccess(category))
                    } else {
                        continuation.resume(Reaction.OnError(Throwable("Not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError?) {
                    println("getCategoryError = ${error?.message}")
                    continuation.resume(
                        Reaction.OnError(Throwable(error?.message, error?.toException()?.cause))
                    )
                }
            })
    }
}

