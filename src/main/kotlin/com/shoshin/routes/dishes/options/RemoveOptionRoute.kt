package com.shoshin.routes.dishes.options

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeOptionRoute() {
    delete("dishes/{dishId}/options/{optionId}") {
        val dishId = call.parameters["dishId"] ?: return@delete call.badRequest()
        val optionId = call.parameters["optionId"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@delete call.forbidden()
        } else {
            val option = OptionsRepo.getOption(dishId, optionId)
            OptionsRepo.removeOption(dishId, optionId)
            call.ok(option)
        }
    }
}