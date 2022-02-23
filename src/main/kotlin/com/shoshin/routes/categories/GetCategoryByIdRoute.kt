package com.shoshin.routes.categories

import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getCategoryById() {
    get("/categories/{id}") {
        println("GET: /category/{id}")
        val id = call.parameters["id"] ?: return@get call.badRequest()
        val category = CategoriesRepo.getCategoryById(id)
        return@get call.ok(category)
    }
}

