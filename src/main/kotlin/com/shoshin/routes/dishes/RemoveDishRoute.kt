package com.shoshin.routes.dishes

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

fun Route.removeDishRoute() {
    delete("categories/{categoryId}/dishes/{dishId}") {
        val categoryId = call.parameters["categoryId"] ?: return@delete call.badRequest()
        val dishId = call.parameters["dishId"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()

        when(val checkRoleRes = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(checkRoleRes.data) {
                    when(DishesRepo.removeDishFromCategory(categoryId, dishId)) {
                        is Reaction.Success -> {
                            when(DishesRepo.removeDish(dishId)) {
                                is Reaction.Success -> return@delete call.ok(dishId)
                                is Reaction.Error -> return@delete call.internalServerError()
                            }
                        }
                        is Reaction.Error -> return@delete call.internalServerError()
                    }
                } else return@delete call.forbidden()
            }
            is Reaction.Error -> return@delete call.internalServerError()
        }


    }
}