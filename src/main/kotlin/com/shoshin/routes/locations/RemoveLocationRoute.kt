package com.shoshin.routes.locations

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeLocationRoute() {
    delete("/locations/{locationId}") {
        val locationId: String = call.parameters["locationId"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()
        val location = LocationsRepo.getLocation(principal.userId, locationId)
        LocationsRepo.removeLocation(principal.userId, locationId)
        return@delete call.ok(location)
    }
}