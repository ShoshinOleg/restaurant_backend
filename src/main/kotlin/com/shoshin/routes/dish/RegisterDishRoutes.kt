package com.shoshin.routes.dish

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

val REF_DISHES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
    .child("menu")
    .child("dishes")

fun Application.registerDishesRoutes() {
    routing {
        dishesRoute()
        updateDishRoute()
        authenticate("firebase") {
            setImageForDishRoute()
        }
    }
}