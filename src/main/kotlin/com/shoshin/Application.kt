package com.shoshin

import com.shoshin.firebase.initFirebase
import io.ktor.application.*
import com.shoshin.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module(testing: Boolean = false) {
    initFirebase()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
