package com.shoshin.routes.orders

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.getOrderRoute() {
    get("orders/{orderId}") {
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.internalServerError()
        val orderId = call.parameters["id"] ?: return@get call.badRequest()
        val order = OrdersRepo.getOrder(principal.userId, orderId)
        return@get call.ok(order)
    }
}