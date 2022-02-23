package com.shoshin.routes.categories

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.dishes.DishesRepo
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.removeCategory(){
    delete("/categories/{id}") {
        println("REMOVE: /category/{id}")
        val id = call.parameters["id"] ?: return@delete call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@delete call.internalServerError()
        if(!UsersRepo.checkRole(principal, "admin")) {
            return@delete call.forbidden()
        } else {
            val category = CategoriesRepo.getCategoryById(id)
            for(dishId in DishesRepo.getDishesIdsForCategory(id)) {
                DishesRepo.removeDish(dishId)
            }
            CategoriesRepo.removeCategory(id)
            return@delete call.ok(category)
        }
    }
}