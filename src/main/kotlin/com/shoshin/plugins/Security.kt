package com.shoshin.plugins

import com.google.firebase.FirebaseApp
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.firebase.firebase
import io.ktor.application.*
import io.ktor.auth.*

fun Application.configureSecurity() {
    authentication {
        firebase("firebase", FirebaseApp.getInstance()) {
            validate { credential ->
                FirebasePrincipal(userId = credential.token.uid, credential.token)
            }
        }
    }
}
