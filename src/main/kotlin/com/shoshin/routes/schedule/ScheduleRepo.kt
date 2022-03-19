package com.shoshin.routes.schedule

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.models.schedule.DayWeekSchedule
import com.shoshin.models.schedule.WeekSchedule
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScheduleRepo {
    companion object {
        private val REF_SCHEDULE: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("schedule")

        suspend fun updateDefaultSchedule(weekSchedule: WeekSchedule) : Boolean {
            return suspendCoroutine { continuation ->
                REF_SCHEDULE.child("default")
                    .setValue(weekSchedule) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(true)
                    }
            }
        }

        suspend fun updateDefaultScheduleDay(day: DayWeekSchedule) {
            return suspendCoroutine { continuation ->
                REF_SCHEDULE.child("default")
                    .child("days")
                    .child("${day.dayOfWeek}")
                    .setValue(day) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }
        }

        suspend fun getDefaultSchedule() : WeekSchedule =
            suspendCoroutine { continuation ->
                REF_SCHEDULE.child("default")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            println("onDataChange")
                            println("snapshot=$snapshot")
                            if(snapshot == null) {
                                throw NotFoundException("Default schedule not found")
                            } else {
                                var schedule = snapshot.getValue(WeekSchedule::class.java)
                                println("schedule=$schedule")
                                schedule = schedule ?: WeekSchedule()
                                continuation.resume(schedule)
                            }
                        }

                        override fun onCancelled(error: DatabaseError?)  {
                            println("error")
                            println("error=$error")
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                        }

                    })
            }
    }
}