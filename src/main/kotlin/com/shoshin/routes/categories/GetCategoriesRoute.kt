package com.shoshin.routes.categories

import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getCategoriesRoute() {
    get("/categories") {
        println("GET: /categories")
        val categories = CategoriesRepo.getCategories()
        return@get call.ok(categories)
    }
}