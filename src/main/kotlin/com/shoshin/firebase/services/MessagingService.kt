package com.shoshin.firebase.services

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.shoshin.common.Reaction
import com.shoshin.common.exceptions.InternalServerError
import com.shoshin.firebase.firebaseMessaging
import com.shoshin.models.orders.Order
import com.shoshin.routes.users.UsersRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MessagingService {
    companion object {
        val REF_NEW_ORDERS: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("orders")
            .child("new")

        suspend fun onNewOrder(order: Order) {
            return suspendCoroutine { continuation ->
                CoroutineScope(continuation.context).launch {
                    val admins = UsersRepo.getAdminIds()
                    println("adminsIds=$admins")
                    if(admins.isNotEmpty()) {
                        for(adminId in admins) {
                            println("adminId=$adminId")
                            sendNotificationToAdmin(order, adminId)
                        }
                    } else {
                        println("Don't created at least one admin account")
                    }
                    continuation.resume(Unit)
                }
            }
        }

        private suspend fun sendNotificationToAdmin(order: Order, adminId: String) {
            val tokensRes = UsersRepo.getFcmTokens(adminId)
            println("tokensRes=$tokensRes")
            for(token in tokensRes) {
                sendNotificationToToken(order, token)
            }
        }

        private fun sendNotificationToToken(order: Order, fcmToken: String) {
            try {
                val message = prepareMessage(order, fcmToken)
                println("preparedMessage=$message")
                val response = firebaseMessaging?.send(message)
                println("send notification response = $response")
            } catch (e: Exception) {
                println("send notification exception. e.message=${e.message}")
            }
        }

        private fun prepareMessage(order: Order, fcmToken: String) : Message {
            val dateTimeParts = order.orderTime?.split(" ")
            val datePart = dateTimeParts?.get(0)?.replace(':','.')
            val notification = Notification.builder()
                .setTitle("Новый заказ")
                .setBody("Заказ на $datePart к ${dateTimeParts?.get(1)} " +
                        "на сумму ${order.orderPrice} рублей")
                .build()
            val androidNotification = AndroidNotification.builder()
                .setSound("new_order.ogg")
                .setChannelId("NEW_ORDER_CHANNEL_ID")
                .setPriority(AndroidNotification.Priority.MAX)
                .build()

            val androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(androidNotification)
                .build()

            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .putData(
                    "orderId" , "${order.id}"
                )
                .build()
            return message
        }
    }
}

