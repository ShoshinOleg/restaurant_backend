package com.shoshin.plugins

import com.shoshin.routes.registerOrderRoutes
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

}
