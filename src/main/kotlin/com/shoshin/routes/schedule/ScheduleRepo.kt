package com.shoshin.routes.schedule

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.common.Reaction
import com.shoshin.models.schedule.DayWeekSchedule
import com.shoshin.models.schedule.WeekSchedule
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScheduleRepo {
    companion object {
        private val REF_SCHEDULE: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("schedule")

        suspend fun updateDefaultSchedule(weekSchedule: WeekSchedule) : Reaction<Boolean> {
            return suspendCoroutine { continuation ->
                REF_SCHEDULE
                    .child("default")
                    .setValue(weekSchedule) { error, _ ->
                        if(error != null ) {
                            continuation.resume(
                                Reaction.Error(error.toException())
                            )
                        } else {
                            continuation.resume(
                                Reaction.Success(true)
                            )
                        }

                    }
            }
        }

        suspend fun updateDefaultDaySchedule(dayWeekSchedule: DayWeekSchedule) : Reaction<DayWeekSchedule> {
            return suspendCoroutine { continuation ->

            }
        }

        suspend fun getDefaultSchedule() : Reaction<WeekSchedule> {
            return suspendCoroutine { continuation ->
                REF_SCHEDULE
                    .child("default")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot == null) {
                                continuation.resume(Reaction.Error(Throwable("Not found")))
                            } else {
                                var schedule = snapshot.getValue(WeekSchedule::class.java)
                                if(schedule == null) {
                                    println("schedule==null")
                                    schedule = WeekSchedule()
                                }
                                 continuation.resume(
                                    Reaction.Success(schedule)
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError?) {
                            continuation.resume(
                                Reaction.Error(Throwable(error?.message, error?.toException()?.cause))
                            )
                        }
                    })
            }
        }
    }
}