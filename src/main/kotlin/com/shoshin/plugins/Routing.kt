package com.shoshin.plugins

import com.shoshin.routes.registerDishesRoutes
import com.shoshin.routes.registerMenuCategoriesRoutes
import com.shoshin.routes.registerOrderRoutes
import com.shoshin.routes.registerUserRoutes
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        get("/") {
            println("/")
            call.respondText("Hello World!")
        }
    }
    registerOrderRoutes()
    registerMenuCategoriesRoutes()
    registerDishesRoutes()
    registerUserRoutes()
}
