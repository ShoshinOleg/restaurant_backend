package com.shoshin.routes.schedule

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

        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            ScheduleRepo.updateDefaultSchedule(weekSchedule)
            return@post call.ok()
        }
    }
}