package com.shoshin.routes


import com.google.cloud.storage.Bucket
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.ApiError
import com.shoshin.common.ErrorResponse
import com.shoshin.common.Reaction
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.firebaseStorage
import com.shoshin.models.MenuCategory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
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
        authenticate("firebase") {
            updateCategory()
            updateCategoryImageRoute()
        }
        getCategories()
        getCategoryById()
        removeCategory()
    }
}

fun Route.updateCategory() {
    post("/categories") {
        println("POST: /categories")
        val principal = call.principal<FirebasePrincipal>()
        println("principal!=null = ${principal!=null}")
        println("principal.id=${principal?.userId}")
        val category = call.receive<MenuCategory>()
        println("POST: ::category=$category")
        if(category.id == null) {
            category.id = REF_CATEGORIES.push().key
            println("POST: ::categoryID=${category.id}")
            category.id ?: return@post call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    ApiError(message = "Can't create new id for category")
                )
            )
        }
        println("category=$category")
        when(val result = addCategory(category)) {
            is Reaction.Success -> {
                return@post call.respond(
                    status = HttpStatusCode.Created,
                    result.data
                )

            }
            is Reaction.Error -> {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    ErrorResponse(ApiError(
                        message = "Category not added"
                    ))
                )
            }
        }
    }
}

fun Route.getCategories() {
    get("/categories") {
        println("GET: /categories")
        when(val result = com.shoshin.routes.getCategories()) {
            is Reaction.Success -> {
                return@get call.respond(
                    status = HttpStatusCode.OK,
                    result.data
                )
            }
            is Reaction.Error -> {
                return@get call.respond(
                    status = HttpStatusCode.NotFound,
                    ErrorResponse(
                        ApiError(message = "asd")
                    )
                )
            }
        }
    }
}


fun Route.getCategoryById() {
    get("/categories/{id}") {
        println("GET: /category/{id}")
        val id = call.parameters["id"] ?: return@get call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(
                ApiError(message = "BadRequest")
            )
        )
        println("id = $id")
        when(val result = getCategoryById(id)) {
            is Reaction.Success -> {
                println("result= $result")
                return@get call.respond(
                    status = HttpStatusCode.OK,
                    result.data
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
}

fun Route.removeCategory(){
    delete("/categories/{id}") {
        println("REMOVE: /category/{id}")
        val id = call.parameters["id"] ?: return@delete call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(
                ApiError(message = "BadRequest")
            )
        )
        println("id = $id")
        when(val result = getCategoryById(id)) {
            is Reaction.Success -> {
                println("result= $result")
                when(val removeResult = removeCategory(id)) {
                    is Reaction.Success -> {
                        return@delete call.respondText(
                            status = HttpStatusCode.OK,
                            text = "Категория удалена"
                        )
                    }
                    is Reaction.Error -> {
                        return@delete call.respond(
                            ErrorResponse(
                                ApiError(message = removeResult.exception.message)
                            )
                        )
                    }
                }

            }
            is Reaction.Error -> {
                return@delete call.respond(
                    status = HttpStatusCode.NotFound,
                    ErrorResponse(
                        ApiError(message = "Not Found")
                    )
                )
            }
        }
    }
}

fun Route.updateCategoryImageRoute() {
    post("/categories/{id}/image") {
        val categoryId = call.parameters["id"] ?: return@post call.respond(
            status = HttpStatusCode.BadRequest,
            ErrorResponse(ApiError(message = "BadRequest"))
        )
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.respond(
            status = HttpStatusCode.InternalServerError,
            ErrorResponse(ApiError(message = "InternalServerError"))
        )
        when(val checkRoleRes = checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(checkRoleRes.data) {
                    val multipartData = call.receiveMultipart()
                    var fileName = ""
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.originalFileName as String
                                val fileBytes = part.streamProvider().readBytes()
                                firebaseStorage?.create(
                                    "images/menu/categories/$categoryId",
                                    fileBytes,
                                    "image/jpeg"
                                )
                            }
                        }
                    }
                    return@post call.respond(
                        HttpStatusCode.OK,
                        "Изображение загружено"
                    )
                } else {
                    return@post call.respond(
                        status = HttpStatusCode.Forbidden,
                        "У вас нет прав администратора"
                    )
                }
            }
            is Reaction.Error -> {
                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    ErrorResponse(ApiError(message = "InternalServerError"))
                )
            }
        }

    }
}

//suspend fun updateCategoryImage() {
//
//    //        fun savePhoto(category: MenuCategory, uri: Uri?, callback: (uri: String?) -> Unit) {
//    //            if(uri != null) {
//    //                val path =
//    //                    REF_STORAGE_CATEGORY_IMAGE
//    //                        .child(category.id!!)
//    //                path.putFile(uri).addOnCompleteListener { task1 ->
//    //                    if(task1.isSuccessful) {
//    //                        path.downloadUrl.addOnCompleteListener { task2 ->
//    //                            if(task2.isSuccessful) {
//    //                                val photoUrl = task2.result.toString()
//    //                                category.imageURL = photoUrl
//    //                                updateCategory(category) {
//    //                                    Log.d("url=", category.imageURL.toString())
//    //                                    callback(photoUrl)
//    //                                }
//    //                            } else {
//    //                                callback(null)
//    //                            }
//    //                        }
//    //                    } else {
//    //                        callback(null)
//    //                    }
//    //                }
//    //            }
//    //        }
//}

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



