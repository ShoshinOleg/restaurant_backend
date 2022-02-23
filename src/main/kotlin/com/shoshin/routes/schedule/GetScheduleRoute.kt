package com.shoshin.routes.schedule

import com.shoshin.common.default_responses.ok
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getDefaultScheduleRoute() {
    get("schedules/default") {
        val schedule = ScheduleRepo.getDefaultSchedule()
        return@get call.ok(schedule)
    }
}