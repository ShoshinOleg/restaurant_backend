package com.shoshin.routes.dishes.options.variants

import com.shoshin.common.Reaction
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

        when(val isAdmin = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(!isAdmin.data) {
                    return@delete call.forbidden()
                } else {
                    when(VariantsRepo.removeVariant(dishId, optionId, variantId)) {
                        is Reaction.Success -> return@delete call.ok()
                        is Reaction.Error -> return@delete call.internalServerError()
                    }
                }
            }
            is Reaction.Error -> return@delete call.internalServerError()
        }
    }
}