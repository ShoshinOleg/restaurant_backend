package com.shoshin.routes.orders

import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.orders.Order
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.makeOrder() {
    post("orders") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()

        println("o1")
        println("call.request = ${call.request}")
//        call.response
        val order = call.receive<Order>()
        println("o2")
        if(principal.userId != order.customerId) {
            println("o3")
            return@post call.forbidden()
        } else {
            println("o4")
            order.id = OrdersRepo.newId()
            println("o5")
            OrdersRepo.updateOrder(order)
            println("o6")
            return@post call.ok(order.getOrderMetaData())
        }
    }
}