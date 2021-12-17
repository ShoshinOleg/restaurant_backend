package com.shoshin.routes.dishes.options

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.dishes.DishOption
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.addOptionRoute() {
    post("dishes/{dishId}/option") {
        val dishId = call.parameters["dishId"] ?: return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val option = call.receive<DishOption>()

        when(val isAdmin = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(isAdmin.data) {
                    when(OptionsRepo.addOption(dishId, option)) {
                        is Reaction.Success -> return@post call.ok()
                        is Reaction.Error -> return@post call.internalServerError()
                    }
                } else return@post call.forbidden()
            }
            is Reaction.Error -> return@post call.internalServerError()
        }
    }
}