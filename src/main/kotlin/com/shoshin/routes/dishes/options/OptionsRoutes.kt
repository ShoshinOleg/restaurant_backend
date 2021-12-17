package com.shoshin.routes.dishes.options

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerDishesOptionsRoutes() {
    routing {
        authenticate("firebase") {
            addOptionRoute()
            removeOptionRoute()
        }
    }
}