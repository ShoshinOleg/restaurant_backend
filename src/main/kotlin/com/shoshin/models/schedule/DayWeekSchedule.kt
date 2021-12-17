package com.shoshin.models.schedule

import kotlinx.serialization.Serializable

@Serializable
data class DayWeekSchedule (
    var dayOfWeek: Int? = null,
    var nameDay: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var isNotWork: Boolean = false
)