package com.shoshin.routes.users

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.setNotificationStatusRoute() {
    post("setNotificationsStatus") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val fcmToken = call.receive<String>()
        val statusString = call.parameters["status"]
        val status = statusString == "true"
        if(status) {
            UsersRepo.setFcmTokenIsEnabled(principal, fcmToken, status)
        } else {
            UsersRepo.removeFcmToken(principal.userId, fcmToken)
        }
        call.ok()
    }
}