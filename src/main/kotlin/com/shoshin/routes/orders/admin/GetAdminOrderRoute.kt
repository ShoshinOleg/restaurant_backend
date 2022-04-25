package com.shoshin.routes.orders.admin

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.orders.OrdersRepo
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.getAdminOrderRoute() {
    get("admin/order") {
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.internalServerError()
        val userId = call.parameters["userId"] ?: return@get call.badRequest()
        val orderId = call.parameters["orderId"] ?: return@get call.badRequest()

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@get call.forbidden()
        } else {
            val order = OrdersRepo.getOrderByUserIdAndOrderId(userId, orderId)
            return@get call.ok(order)
        }
    }
}