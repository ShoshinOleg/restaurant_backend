package com.shoshin.routes.categories

import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
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

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            category.id = category.id ?: CategoriesRepo.newCategoryId()
            CategoriesRepo.addCategory(category)
            return@post call.ok(category)
        }
    }
}