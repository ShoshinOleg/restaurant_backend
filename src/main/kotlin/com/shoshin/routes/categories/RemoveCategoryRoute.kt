package com.shoshin.routes.categories

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.*
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeCategory(){
    delete("/categories/{id}") {
        println("REMOVE: /category/{id}")
        val id = call.parameters["id"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()
        when(val checkRoleRes = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(checkRoleRes.data) {
                    when(CategoriesRepo.getCategoryById(id)) {
                        is Reaction.Success -> {
                            when(val removeResult = CategoriesRepo.removeCategory(id)) {
                                is Reaction.Success -> return@delete call.ok("Категория удалена")
                                is Reaction.Error -> {
                                    return@delete call.internalServerError(
                                        removeResult.exception.message?: "Internal Server Error"
                                    )
                                }
                            }
                        }
                        is Reaction.Error -> return@delete call.notFound()
                    }
                } else return@delete call.forbidden()
            }
            is Reaction.Error -> return@delete call.internalServerError()
        }
    }
}