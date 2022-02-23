package com.shoshin.routes.dishes.options.variants

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.dishes.DishOptionVariant
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.addVariantRoute() {
    post("dishes/{dishId}/options/{optionId}/variants") {
        val dishId = call.parameters["dishId"] ?: return@post call.badRequest()
        val optionId = call.parameters["optionId"] ?: return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val variant = call.receive<DishOptionVariant>()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            VariantsRepo.addVariant(dishId, optionId, variant)
            return@post call.ok(variant)
        }
    }
}