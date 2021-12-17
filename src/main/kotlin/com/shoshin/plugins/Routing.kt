package com.shoshin.plugins

import com.shoshin.routes.categories.registerCategoriesRoutes
import com.shoshin.routes.dishes.options.registerDishesOptionsRoutes
import com.shoshin.routes.dishes.options.variants.registerVariantsRoutes
import com.shoshin.routes.dishes.registerDishesRoutes
import com.shoshin.routes.orders.registerOrderRoutes
import com.shoshin.routes.schedule.scheduleRoutes
import com.shoshin.routes.users.registerUserRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {

    routing {
        get("/") {
            println("/")
            call.respondText("Hello World!")
        }
    }
    registerOrderRoutes()
    registerUserRoutes()
    registerCategoriesRoutes()
    registerDishesRoutes()
    registerDishesOptionsRoutes()
    registerVariantsRoutes()
    scheduleRoutes()
}
