package com.shoshin.routes.users

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerUserRoutes() {
    routing {
        authenticate("firebase") {
            registerSignInUserRoute()
            hasRoleRoute()
            updateUserRoute()
        }
    }
}