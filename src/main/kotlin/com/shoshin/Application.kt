package com.shoshin

import com.shoshin.firebase.initFirebase
import com.shoshin.plugins.*
import io.ktor.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module(testing: Boolean = false) {
    initFirebase()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureStatusPage()
}
