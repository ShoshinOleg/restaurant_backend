package com.shoshin.firebase

import io.ktor.auth.*

data class FirebasePrincipal(
    val userId: String
) : Principal