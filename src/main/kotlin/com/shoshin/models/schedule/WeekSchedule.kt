package com.shoshin.models.schedule

import kotlinx.serialization.Serializable

@Serializable
data class WeekSchedule (
    val days: List<DayWeekSchedule> = listOf()
)