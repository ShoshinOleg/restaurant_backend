package com.shoshin.routes

import com.shoshin.models.orderStorage
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.registerOrderRoutes() {
    routing {
        authenticate("firebase") {
            listOrdersRoute()
        }
    }
}

fun Route.listOrdersRoute() {
    get("/order") {
        println("/order")
        if(orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        }
    }
}