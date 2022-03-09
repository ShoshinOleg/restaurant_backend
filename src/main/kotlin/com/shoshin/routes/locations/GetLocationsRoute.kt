package com.shoshin.routes.locations

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.getLocationsRoute() {
    get("/locations") {
        println("GET: /locations")
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.internalServerError()
        val locations = LocationsRepo.getLocations(principal.userId)
        return@get call.ok(locations)
    }
}