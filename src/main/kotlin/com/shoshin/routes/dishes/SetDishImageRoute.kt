package com.shoshin.routes.dishes

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.firebaseStorage
import com.shoshin.firebase.utils.sendFileToStorage
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import java.util.*

fun Route.setImageForDishRoute() {
    post("/dishes/{dishId}/image") {
        val dishId = call.parameters["dishId"] ?:  return@post call.badRequest()
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            val targetUrl = call.sendFileToStorage(
                "images/menu/dishes/$dishId-${UUID.randomUUID()}",
                "image/png"
            )
            val dish = DishesRepo.getDish(dishId)
            if(dish.imageUrl != null) {
                val list = dish.imageUrl!!.split("/b/restaurant-48d90.appspot.com/o/", "?alt=media")
                if(list.size >= 2) {
                    val filePath = java.net.URLDecoder.decode(list[1], "utf-8")
                    firebaseStorage?.get(filePath)?.delete()
                }
            }
            DishesRepo.setDishImage(dishId, targetUrl)
            return@post call.ok(targetUrl)
        }
    }
}