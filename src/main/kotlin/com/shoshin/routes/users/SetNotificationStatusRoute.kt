package com.shoshin.routes.users

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.firebase.FirebasePrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.setNotificationStatusRoute() {
    post("setNotificationsStatus") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val fcmToken = call.receive<String>()
        val status = call.parameters["status"]

        println("fcmToken=$fcmToken")
        println("status=$status")

        //call.receive<RestaurantUser>()
    }
}