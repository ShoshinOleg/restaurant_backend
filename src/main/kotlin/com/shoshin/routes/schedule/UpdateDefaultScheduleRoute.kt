package com.shoshin.routes.schedule

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.schedule.WeekSchedule
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.updateDefaultScheduleRoute() {
    post("schedules/default") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val weekSchedule = call.receive<WeekSchedule>()

        when(val isAdmin = UsersRepo.checkRole(principal, "admin")) {
            is Reaction.Error -> return@post call.internalServerError()
            is Reaction.Success -> {
                if(!isAdmin.data) {
                    return@post call.forbidden()
                } else {
                    when(ScheduleRepo.updateDefaultSchedule(weekSchedule)) {
                        is Reaction.Error -> return@post call.internalServerError()
                        is Reaction.Success -> return@post call.ok()
                    }
                }
            }

        }
    }
}