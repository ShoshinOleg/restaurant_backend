package com.shoshin.routes.orders

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.registerOrderRoutes() {
    routing {
        authenticate("firebase") {
//            listOrdersRoute()
        }
    }
}
