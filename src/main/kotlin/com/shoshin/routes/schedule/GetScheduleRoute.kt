package com.shoshin.routes.schedule

import com.shoshin.common.Reaction
import com.shoshin.common.default_responses.internalServerError
import com.shoshin.common.default_responses.ok
import com.shoshin.models.schedule.WeekSchedule
import io.ktor.application.*
import io.ktor.routing.*

fun Route.getDefaultScheduleRoute() {
    get("schedules/default") {
        when(val result = ScheduleRepo.getDefaultSchedule()) {
            is Reaction.Error -> return@get call.internalServerError()
            is Reaction.Success -> return@get call.ok(result.data)
        }
    }
}