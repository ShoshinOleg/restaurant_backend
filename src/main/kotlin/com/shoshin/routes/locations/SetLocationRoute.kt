package com.shoshin.routes.locations

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.Location
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.setLocation() {
    post("/locations") {
        println("POST: /locations")
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val location = call.receive<Location>()
        location.id = location.id ?: LocationsRepo.newId(principal.userId)
        LocationsRepo.setLocation(principal.userId, location)
        return@post call.ok(location)
    }
}