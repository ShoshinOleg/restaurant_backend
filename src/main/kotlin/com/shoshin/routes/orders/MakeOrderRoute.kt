package com.shoshin.routes.orders

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FCM_API_KEY
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.fcmService
import com.shoshin.firebase.firebaseMessaging
import com.shoshin.firebase.http_client.fcm.FcmMessage
import com.shoshin.firebase.http_client.fcm.FcmNotification
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


//            val message = Message.builder()
//                .setToken("cUnZ959RQHeM_aYHQyz2gi:APA91bEpgiTm6XNKMJq_WH4JD91RbhOInT77oxu6UJjXvU-B4ArThbAdXWZx8U_KmpMe3iOIOlynM8LR3c8M8ACO3TqpSt63tDXl0fQkTGzjsPLjEDjEDG5tMBaPfhkxNPSu0-4aFoZ8")
//                .setNotification(notification)
////                .setAndroidConfig(androidConfig)
//                .putData(
//                    "orderId" , "${order.id}"
//                )
//                .build()
            val dateTimeParts = order.orderTime?.split(" ")
            val datePart = dateTimeParts?.get(0)?.replace(':','.')
            val fcmMessage = FcmMessage(
                to ="cUnZ959RQHeM_aYHQyz2gi:APA91bEpgiTm6XNKMJq_WH4JD91RbhOInT77oxu6UJjXvU-B4ArThbAdXWZx8U_KmpMe3iOIOlynM8LR3c8M8ACO3TqpSt63tDXl0fQkTGzjsPLjEDjEDG5tMBaPfhkxNPSu0-4aFoZ8",
                notification = FcmNotification(
                    title = "Новый заказ",
                    body = "\"Заказ на $datePart к ${dateTimeParts?.get(1)}" +
                            "на сумму ${order.orderPrice} рублей\""
                ),
                data = buildMap {
                    put("orderId" , "${order.id}")
                }
            )
            println("FCM_API_KEY=$FCM_API_KEY")
            println("Bearer $FCM_API_KEY")
            fcmService?.sendMessage(
                "key=$FCM_API_KEY",
                fcmMessage
            )
//            val testObject = testService?.getTest()
//            println("test object=$testObject")
//            MessagingService.onNewOrder(order)
            println("tut2")
            return@post call.ok(order.getOrderMetaData())
        }
    }
}