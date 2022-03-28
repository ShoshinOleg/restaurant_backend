package com.shoshin.routes.orders

import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.common.exceptions.ForbiddenError
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.services.MessagingService
import com.shoshin.models.orders.Order
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.makeOrder() {
    post("orders") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val order = call.receive<Order>()
        if(principal.userId != order.customerId) {
            return@post call.forbidden()
        } else {
            order.id = OrdersRepo.newId()
            OrdersRepo.updateOrder(order)
            MessagingService.onNewOrder(order)
            return@post call.ok(order.getOrderMetaData())
        }
    }
}