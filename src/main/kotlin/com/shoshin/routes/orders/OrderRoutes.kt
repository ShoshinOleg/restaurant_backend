package com.shoshin.routes.orders

import com.shoshin.routes.orders.admin.getAdminOrderRoute
import com.shoshin.routes.orders.admin.getAdminOrdersRoute
import com.shoshin.routes.orders.user.getOrderRoute
import com.shoshin.routes.orders.user.getOrdersMetadataRoute
import com.shoshin.routes.orders.user.makeOrder
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerOrderRoutes() {
    routing {
        authenticate("firebase") {
            getOrdersMetadataRoute()
            makeOrder()
            getOrderRoute()

            getAdminOrderRoute()
            getAdminOrdersRoute()
        }
    }
}
