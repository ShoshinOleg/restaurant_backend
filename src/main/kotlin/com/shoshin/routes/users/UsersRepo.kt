package com.shoshin.routes.users

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.Reaction
import com.shoshin.firebase.FirebasePrincipal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UsersRepo {
    companion object {
        val REF_USERS : DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("users")

        val REF_ROLES: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("roles")

        suspend fun onSignUser(principal: FirebasePrincipal): Reaction<Unit> {
            return suspendCoroutine { continuation ->
                REF_USERS.child(principal.userId)
                    .child("id")
                    .setValue(principal.userId) { error, ref ->
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

        suspend fun checkRole(principal: FirebasePrincipal, role: String): Reaction<Boolean> {
            return suspendCoroutine {  cont ->
                REF_ROLES.child(role)
                    .child(principal.userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val isAdmin = snapshot?.getValue(Boolean::class.java)
                            cont.resume(
                                Reaction.Success(isAdmin!!)
                            )
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            cont.resume(
                                Reaction.Success(false)
                            )
                        }
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
    }
}