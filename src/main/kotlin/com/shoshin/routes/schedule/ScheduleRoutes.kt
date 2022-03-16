package com.shoshin.routes.schedule

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.scheduleRoutes() {
    routing {
        getDefaultScheduleRoute()
        authenticate("firebase") {
            updateDefaultScheduleRoute()
            updateDefaultScheduleDayRoute()
        }
    }
}