package com.shoshin.routes.dishes.options.variants

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerVariantsRoutes() {
    routing {
        authenticate("firebase") {
            addVariantRoute()
            removeVariantRoute()
        }
    }
}