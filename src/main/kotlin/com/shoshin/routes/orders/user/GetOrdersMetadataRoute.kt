package com.shoshin.routes.orders.user

import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.getOrdersMetadataRoute() {
    get("orders/metadata") {
        val principal = call.principal<FirebasePrincipal>() ?: return@get call.internalServerError()
        val ordersMetadata = UsersRepo.getOrdersMetadata(principal.userId)
        return@get call.ok(ordersMetadata)
    }
}