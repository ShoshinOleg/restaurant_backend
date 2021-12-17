package com.shoshin.routes.dishes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerDishesRoutes() {
    routing {
        dishesRoute()
        updateDishRoute()
        authenticate("firebase") {
            setImageForDishRoute()
            removeDishRoute()
        }
    }
}