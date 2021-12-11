package com.shoshin.firebase

import com.google.firebase.auth.FirebaseToken
import io.ktor.auth.*

data class FirebasePrincipal(
    val userId: String,
    val token: FirebaseToken
) : Principal