package com.shoshin.routes.orders

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.models.orders.Order
import com.shoshin.models.orders.OrderMetadata
import com.shoshin.routes.users.UsersRepo
import io.ktor.features.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OrdersRepo {
    companion object {
        private val REF_ORDERS: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("orders")

        fun newId() = REF_ORDERS.child("new").push().key.toString()

        suspend fun updateOrder(order: Order) {
            return suspendCoroutine { continuation ->
                REF_ORDERS
                    .child(order.orderDate)
                    .child(order.status)
                    .child(order.id)
                    .setValue(order) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else {
                            CoroutineScope(continuation.context + Dispatchers.IO).launch {
                                UsersRepo.setOrder(order)
                                continuation.resume(Unit)
                            }
                        }
                    }
            }
        }

        suspend fun getOrder(userId: String, orderId: String): Order {
            return suspendCoroutine { continuation ->
                CoroutineScope(continuation.context + Dispatchers.IO).launch {
                    val orderMetadata = UsersRepo.getOrderMetadata(userId, orderId)
                    REF_ORDERS
                        .child(orderMetadata.date)
                        .child(orderMetadata.status)
                        .child(orderMetadata.id)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                if(snapshot != null) {
                                    continuation.resume(snapshot.getValue(Order::class.java))
                                } else {
                                    throw NotFoundException()
                                }
                            }

                            override fun onCancelled(error: DatabaseError?) {
                                throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                            }
                        })
                }
            }
        }
    }
}

//        suspend fun updateDefaultSchedule(weekSchedule: WeekSchedule) : Boolean {
//            return suspendCoroutine { continuation ->
//                REF_SCHEDULE.child("default")
//                    .setValue(weekSchedule) { error, _ ->
//                        if(error != null )
//                            throw error.toException()
//                        else
//                            continuation.resume(true)
//                    }
//            }
//        }

//class OrderRepo {
//    companion object {
//        val REF_ORDERS =
//            Firebase.database.reference
//                .child("order")
//
//        fun newId() = REF_ORDERS.push().key.toString()
//
//        fun updateOrder(order: Order, callback: () -> Unit) {
//            REF_ORDERS
//                .child(order.orderDate!!)
//                .child(order.status!!)
//                .child(order.id!!)
//                .setValue(order)
//                .addOnSuccessListener {
//                    UserRepo.setOrder(order) {
//                        callback()
//                    }
//                }
//        }
//
//        fun getOrder(orderMetadata: OrderMetadata, callback: (order: Order) -> Unit) {
////            val executingState = if (orderMetadata.isExecuted) "executed" else "unexecuted"
//            REF_ORDERS
//                .child(orderMetadata.date!!)
//                .child(orderMetadata.status!!)
//                .child(orderMetadata.id!!)
//                .addListenerForSingleValueEvent(object: ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val order = snapshot.getValue(Order::class.java)
//                        callback(order!!)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {}
//                })
//
//        }
//
//
//
////        fun getOrders(callback: (MutableList<Order>) -> Unit) {
////            REF_ORDERS
////                .addListenerForSingleValueEvent(object: ValueEventListener{
////                    override fun onDataChange(snapshot: DataSnapshot) {
////                        val dict = snapshot.getValue(Dictionary<String, Order>::class.java)
////                    }
////
////                    override fun onCancelled(error: DatabaseError) {}
////
////                })
////        }
//    }
//}