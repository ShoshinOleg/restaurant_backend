package com.shoshin.routes.schedule

import com.shoshin.common.default_responses.forbidden
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.firebase.FirebasePrincipal
import com.shoshin.models.schedule.DayWeekSchedule
import com.shoshin.routes.users.UsersRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.updateDefaultScheduleDayRoute() {
    post("schedules/default/day") {
        val principal = call.principal<FirebasePrincipal>() ?: return@post call.internalServerError()
        val daySchedule = call.receive<DayWeekSchedule>()
        if(!UsersRepo.checkRole(principal, "admin")) {
            return@post call.forbidden()
        } else {
            ScheduleRepo.updateDefaultScheduleDay(daySchedule)
            return@post call.ok(daySchedule)
        }
    }
}