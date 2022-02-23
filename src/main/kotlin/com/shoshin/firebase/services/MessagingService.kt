package com.shoshin.firebase.services

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.shoshin.common.Reaction
import com.shoshin.firebase.firebaseMessaging
import com.shoshin.models.orders.Order
import com.shoshin.routes.users.UsersRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessagingService {
    companion object {
        val REF_NEW_ORDERS: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("orders")
            .child("new")
    }

    init {
        REF_NEW_ORDERS
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                    if(snapshot == null) {
                        //ошибка
                    } else {
                        val order = snapshot.getValue(Order::class.java)
                        CoroutineScope(Dispatchers.Default).launch {
                            onNewOrder(order)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot?, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot?) {}
                override fun onChildMoved(snapshot: DataSnapshot?, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError?) {}
            })
    }

    suspend fun onNewOrder(order: Order) {
        when(val admins = UsersRepo.getAdminIds()) {
            is Reaction.Error -> {
                println("Can't get admins ids")
                return
            }
            is Reaction.Success -> {
                if(admins.data.isEmpty()) {
                    println("Don't created at least one admin account")
                } else {
                    for(adminId in admins.data) {
                        sendNotificationToAdmin(order, adminId)
                    }
                }
            }
        }
    }

    private suspend fun sendNotificationToAdmin(order: Order, adminId: String, ) {
        when(val tokensRes = UsersRepo.getFcmTokens(adminId)) {
            is Reaction.Error -> println("Can't get admin tokens")
            is Reaction.Success -> {
                for(token in tokensRes.data) {
                    sendNotificationToToken(order, token)
                }
            }
        }
    }

    private fun sendNotificationToToken(order: Order, fcmToken: String) {
        val message = prepareMessage(order, fcmToken)
        val response = firebaseMessaging?.send(message)
        println("send notification response = $response")
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

