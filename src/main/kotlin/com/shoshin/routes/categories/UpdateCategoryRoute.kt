package com.shoshin.routes.categories

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.created
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.MenuCategory
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.updateCategory() {
    post("/categories") {
        println("POST: /categories")
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val category = call.receive<MenuCategory>()

        when(val checkRoleRes = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Success -> {
                if(!checkRoleRes.data) {
                    return@post call.forbidden()
                } else {
                    if(category.id == null) {
                        category.id = REF_CATEGORIES.push().key
                        category.id ?: return@post call.internalServerError(
                            "Can't create new id for category"
                        )
                    }
                    println("category=$category")
                    when(val result = CategoriesRepo.addCategory(category)) {
                        is Reaction.Success -> return@post call.created(result.data)
                        is Reaction.Error -> return@post call.internalServerError("Category not added")
                    }
                }
            }
            is Reaction.Error -> return@post call.internalServerError()
        }
    }
}