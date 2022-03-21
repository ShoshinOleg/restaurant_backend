package com.shoshin.routes.users

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.Reaction
import com.shoshin.common.exceptions.ForbiddenError
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.orders.Order
import com.shoshin.models.orders.OrderMetadata
import com.shoshin.models.users.RestaurantUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UsersRepo {
    companion object {
        val REF_USERS : DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("users")

        val REF_ROLES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("roles")

        suspend fun updateUser(principal: FirebasePrincipal, user: RestaurantUser): Unit =
            suspendCoroutine { continuation ->
                if(principal.userId != user.id) throw ForbiddenError()
                REF_USERS.child(principal.userId)
                    .setValue(user) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }

        suspend fun onSignUser(principal: FirebasePrincipal) {
            return suspendCoroutine { continuation ->
                REF_USERS.child(principal.userId)
                    .child("id")
                    .setValue(principal.userId) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }
        }

        suspend fun checkRole(principal: FirebasePrincipal, role: String): Boolean {
            return suspendCoroutine {  cont ->
                REF_ROLES.child(role)
                    .child(principal.userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) =
                            cont.resume(snapshot?.getValue(Boolean::class.java)!!)

                        override fun onCancelled(error: DatabaseError?) = cont.resume(false)
                    })
            }
        }

        suspend fun getFcmTokens(userId: String) : Reaction<List<String>> {
            return suspendCoroutine { continuation ->
                REF_USERS
                    .child(userId)
                    .child("fcmTokens")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val tokens = mutableListOf<String>()
                            if(snapshot != null) {
                                for(tokenSnap in snapshot.children) {
                                    val token = tokenSnap.getValue(String::class.java)
                                    tokens.add(token)
                                }
                            }
                            continuation.resume(Reaction.Success(tokens))
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            continuation.resume(
                                Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
                            )
                        }

                    })
            }
        }

        suspend fun enableFcmToken(principal: FirebasePrincipal, fcmToken: String) : Reaction<Unit> {
            return setFcmTokenIsEnabled(principal, fcmToken, true)
        }

        suspend fun disableFcmToken(principal: FirebasePrincipal, fcmToken: String) : Reaction<Unit> {
            return setFcmTokenIsEnabled(principal, fcmToken, false)
        }

        private suspend fun setFcmTokenIsEnabled(
            principal: FirebasePrincipal,
            fcmToken: String,
            isEnabled: Boolean
        ): Reaction<Unit> {
            return suspendCoroutine { continuation ->
                REF_USERS
                    .child(principal.userId)
                    .child("fcmTokens")
                    .child(fcmToken)
                    .setValue(isEnabled) { error, _ ->
                        if(error != null ) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(Unit)
                            )
                        }
                    }
            }
        }

        suspend fun removeFcmToken(userId: String, fcmToken: String) : Reaction<Unit> {
            return suspendCoroutine { continuation ->
                REF_USERS
                    .child(userId)
                    .child("fcmTokens")
                    .child(fcmToken)
                    .removeValue { error, _ ->
                        if(error != null ) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(Unit)
                            )
                        }
                    }
            }
        }

        suspend fun getAdminIds() : Reaction<List<String>>{
            return suspendCoroutine { continuation ->
                REF_ROLES
                    .child("admin")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val list = mutableListOf<String>()
                            if(snapshot != null) {
                                for(child in snapshot.children) {
                                    list.add(child.getValue(String::class.java))
                                }
                            }
                            continuation.resume(Reaction.Success(list))
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            continuation.resume(
                                Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
                            )
                        }
                    })

            }
        }

        suspend fun setOrder(order: Order) {
            return suspendCoroutine { continuation ->
                REF_USERS
                    .child(order.customerId)
                    .child("ordersInfo")
                    .child(order.id)
                    .setValue(order.getOrderMetaData()) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }
        }

        suspend fun getOrdersMetadata(userId: String): List<OrderMetadata> {
            return suspendCoroutine { continuation ->
                REF_USERS
                    .child(userId)
                    .child("ordersInfo")
                    .addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val ordersMetadata = mutableListOf<OrderMetadata>()
                            if(snapshot != null) {
                                for (child in snapshot.children) {
                                    ordersMetadata.add(child.getValue(OrderMetadata::class.java))
                                }
                            }
                            continuation.resume(ordersMetadata)
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                        }
                    })
            }
        }
    }
}