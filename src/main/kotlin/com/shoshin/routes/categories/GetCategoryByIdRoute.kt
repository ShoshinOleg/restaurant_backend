package com.shoshin.routes.categories

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.badRequest
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getCategoryById() {
    get("/categories/{id}") {
        println("GET: /category/{id}")
        val id = call.parameters["id"] ?: return@get call.badRequest()
        println("id = $id")
        when(val result = CategoriesRepo.getCategoryById(id)) {
            is Reaction.Success -> return@get call.ok(result.data)
            is Reaction.Error -> return@get call.internalServerError()
        }
    }
}

