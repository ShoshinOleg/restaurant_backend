package com.shoshin.plugins

import com.google.firebase.FirebaseApp
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.firebase
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureSecurity() {
    authentication {
        firebase("firebase", FirebaseApp.getInstance()) {
            validate { credential ->
                FirebasePrincipal(userId = credential.token.uid, credential.token)
            }
        }
    }
}
