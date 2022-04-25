package com.shoshin.routes.orders.user

import com.google.gson.Gson
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.*
import com.shoshin.firebase.http_client.fcm.FcmMessage
import com.shoshin.firebase.http_client.fcm.FcmNotification
import com.shoshin.firebase.http_client.fcm_v1.*
import com.shoshin.models.orders.Order
import com.shoshin.routes.orders.OrdersRepo
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
            println("tut1")

            sendV1Message(order)

            println("tut2")
            return@post call.ok(order.getOrderMetaData())
        }
    }
}

suspend fun sendV1Message(order: Order) {
    val dateTimeParts = order.orderTime?.split(" ")
    val datePart = dateTimeParts?.get(0)?.replace(':','.')
    val fcmMessage = FcmV1Message(
        token ="cUnZ959RQHeM_aYHQyz2gi:APA91bEpgiTm6XNKMJq_WH4JD91RbhOInT77oxu6UJjXvU-B4ArThbAdXWZx8U_KmpMe3iOIOlynM8LR3c8M8ACO3TqpSt63tDXl0fQkTGzjsPLjEDjEDG5tMBaPfhkxNPSu0-4aFoZ8",
        notification = FcmV1Notification(
            title = "Новый заказ",
            body = "Заказ на $datePart к ${dateTimeParts?.get(1)}" +
                    "на сумму ${order.orderPrice} рублей"
        ),
        data = mapOf(
            "userId" to "${order.customerId}",
            "orderId" to "${order.id}"
        ),
        android = FcmV1Android(
            FcmV1AndroidNotification(
                click_action = ".MainActivity"
            )
        )
    )

    println(
        "fcmMessage = $fcmMessage"
    )
    val string = Gson().toJson(fcmMessage)
    println("string=$string")
    val accessToken = getAccessToken()
    println("accessToken=$accessToken")
    fcmV1Service?.sendMessage(
        "Bearer $accessToken",
        FcmV1Wrapper(
            fcmMessage
        )
    )
}

suspend fun sendMessage(order: Order) {
    val dateTimeParts = order.orderTime?.split(" ")
    val datePart = dateTimeParts?.get(0)?.replace(':','.')
    val fcmMessage = FcmMessage(
        to ="cUnZ959RQHeM_aYHQyz2gi:APA91bEpgiTm6XNKMJq_WH4JD91RbhOInT77oxu6UJjXvU-B4ArThbAdXWZx8U_KmpMe3iOIOlynM8LR3c8M8ACO3TqpSt63tDXl0fQkTGzjsPLjEDjEDG5tMBaPfhkxNPSu0-4aFoZ8",
        notification = FcmNotification(
            title = "Новый заказ",
            body = "\"Заказ на $datePart к ${dateTimeParts?.get(1)}" +
                    "на сумму ${order.orderPrice} рублей\""
        ),
        data = mapOf(
            "orderId" to "${order.id}"
        )
    )
    println("FCM_API_KEY=$FCM_API_KEY")
    println("Bearer $FCM_API_KEY")
    fcmService?.sendMessage(
        "key=$FCM_API_KEY",
        fcmMessage
    )
}