package com.shoshin.firebase.services

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.messaging.*
import com.shoshin.models.orders.Order
import com.shoshin.routes.users.UsersRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.jvmName


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
            println("sendNotificationToAdmin")
            val tokensRes = UsersRepo.getFcmTokens(adminId)
            println("tokensRes=$tokensRes")
            for(token in tokensRes) {
                sendNotificationToToken(order, token)
            }
        }

        private fun sendNotificationToToken(order: Order, fcmToken: String) {
            try {
                println("sendNotificationToToken")
                println("order=$order")
                println("fcmToken=$fcmToken")
                val message: Message = prepareMessage(order, fcmToken)
                //println("preparedMessage=$message")
//                firebaseMessaging?.sendAsync(message)
                //val response = firebaseMessaging?.send(message)

//                firebaseMessaging?.send

//                val message = Message.builder()
//                    .putData("score", "850")
//                    .putData("time", "2:45")
//                    .setToken(fcmToken)
//                    .build()

// Send a message to the device corresponding to the provided
// registration token.

// Send a message to the device corresponding to the provided
// registration token.
                val response = FirebaseMessaging.getInstance(FirebaseApp.getInstance()).send(message)
// Response is a message ID string.
// Response is a message ID string.
                println("Successfully sent message: $response")

                println("send notification response = $response")
            } catch (e: FirebaseMessagingException) {
//                MessagingErrorCode.
//                MessagingErrorCode.INVALID_ARGUMENT
                println("e::class.simpleName = ${e::class.simpleName}")
                println("e::class.qualifiedName = ${e::class.qualifiedName}")
                println("e::class.jvmName = ${e::class.jvmName}")
                println("send notification exception. e.message=${e.message}")
                println("e=${e}")
                println("e.messagingErrorCode=${e.messagingErrorCode}")
                println("e.message=${e.message}")
                println("ordinal=${e.messagingErrorCode.ordinal}")

                OkHttpClient.Builder()

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

