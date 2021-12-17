package com.shoshin.routes.categories

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

val REF_CATEGORIES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("menu")
    .child("categories")

fun Application.registerCategoriesRoutes() {
    routing {
        authenticate("firebase") {
            updateCategory()
            updateCategoryImageRoute()
            removeCategory()
        }
        getCategoriesRoute()
        getCategoryById()
    }
}