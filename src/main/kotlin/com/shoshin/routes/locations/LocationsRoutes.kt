package com.shoshin.routes.locations

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.locationsRoutes() {
    routing {
        authenticate("firebase") {
            getLocationsRoute()
            setLocation()
            removeLocationRoute()
        }
    }
}