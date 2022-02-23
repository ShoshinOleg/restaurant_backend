package com.shoshin.routes.dishes.options.variants

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeVariantRoute() {
    delete("dishes/{dishId}/options/{optionId}/variants/{variantId}") {
        val dishId = call.parameters["dishId"] ?: return@delete call.badRequest()
        val optionId = call.parameters["optionId"] ?: return@delete call.badRequest()
        val variantId = call.parameters["variantId"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()


        if(!UsersRepo.checkRole(principal, "admin")) {
            return@delete call.forbidden()
        } else {
            val variant = VariantsRepo.getVariant(dishId, optionId, variantId)
            VariantsRepo.removeVariant(dishId, optionId, variantId)
            return@delete call.ok(variant)
        }
    }
}