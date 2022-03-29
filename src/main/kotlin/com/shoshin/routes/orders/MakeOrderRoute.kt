package com.shoshin.routes.orders

import com.google.firebase.messaging.Message
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FCM_API_KEY
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.fcmService
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
            println("tut1")

            //            val notification = Notification.builder()
            //                .setTitle("Новый заказ")
            //                .setBody("Заказ на $datePart к ${dateTimeParts?.get(1)} " +
            //                        "на сумму ${order.orderPrice} рублей")
            //                .build()
            //            val androidNotification = AndroidNotification.builder()
            //                .setSound("new_order.ogg")
            //                .setChannelId("NEW_ORDER_CHANNEL_ID")
            //                .setPriority(AndroidNotification.Priority.MAX)
            //                .build()
            //
            //            val androidConfig = AndroidConfig.builder()
            //                .setPriority(AndroidConfig.Priority.HIGH)
            //                .setNotification(androidNotification)
            //                .build()
            //
            //            val message = Message.builder()
            //                .setToken(fcmToken)
            //                .setNotification(notification)
            //                .setAndroidConfig(androidConfig)
            //                .putData(
            //                    "orderId" , "${order.id}"
            //                )
            //                .build()

//                    val notification = Notification.builder()
//                        .setTitle("Новый заказ")
//                        .setBody("Заказ на $datePart к ${dateTimeParts?.get(1)} " +
//                                "на сумму ${order.orderPrice} рублей")
//                        .build()

            val message = Message.builder()
                .setToken("cUnZ959RQHeM_aYHQyz2gi:APA91bEpgiTm6XNKMJq_WH4JD91RbhOInT77oxu6UJjXvU-B4ArThbAdXWZx8U_KmpMe3iOIOlynM8LR3c8M8ACO3TqpSt63tDXl0fQkTGzjsPLjEDjEDG5tMBaPfhkxNPSu0-4aFoZ8")
//                .setNotification(notification)
//                .setAndroidConfig(androidConfig)
                .putData(
                    "orderId" , "${order.id}"
                )
                .build()

            fcmService?.sendMessage(
                "Bearer $FCM_API_KEY",
                message
            )
//            val testObject = testService?.getTest()
//            println("test object=$testObject")
//            MessagingService.onNewOrder(order)
            println("tut2")
            return@post call.ok(order.getOrderMetaData())
        }
    }
}