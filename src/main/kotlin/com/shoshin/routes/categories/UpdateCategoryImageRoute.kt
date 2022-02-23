package com.shoshin.routes.categories

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.utils.sendFileToStorage
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.updateCategoryImageRoute() {
    post("/categories/{id}/image") {
        val categoryId = call.parameters["id"] ?: return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            val targetUrl = call.sendFileToStorage(
                "images/menu/categories/$categoryId",
                "image/jpeg"
            )
            CategoriesRepo.setCategoryImage(categoryId, targetUrl)
            return@post call.ok(targetUrl)
        }
    }
}