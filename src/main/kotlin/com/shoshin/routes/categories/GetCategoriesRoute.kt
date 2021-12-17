package com.shoshin.routes.categories

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.notFound
import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getCategoriesRoute() {
    get("/categories") {
        println("GET: /categories")
        when(val result = CategoriesRepo.getCategories()) {
            is Reaction.Success -> return@get call.ok(result.data)
            is Reaction.Error -> return@get call.notFound()
        }
    }
}

