package com.shoshin.routes.locations

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.shoshin.models.Location
import io.ktor.features.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationsRepo {
    companion object {
        val REF_LOCATIONS: DatabaseReference = FirebaseDatabase.getInstance(FirebaseApp.getInstance()).reference
            .child("locations")

        fun newId(userId: String) = REF_LOCATIONS.child(userId).push().key.toString()

        suspend fun getLocations(userId: String): List<Location> =
            suspendCoroutine { continuation ->
                REF_LOCATIONS
                    .child(userId)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            val locations = mutableListOf<Location>()
                            if(snapshot != null) {
                                for(child in snapshot.children)
                                    locations.add(child.getValue(Location::class.java))
                            }
                            continuation.resume(locations)
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }

        suspend fun getLocation(userId: String): Location =
            suspendCoroutine { continuation ->
                REF_LOCATIONS.child(userId)
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if(snapshot != null)
                                continuation.resume(snapshot.getValue(Location::class.java))
                            else
                                throw NotFoundException()
                        }

                        override fun onCancelled(error: DatabaseError?) =
                            throw error?.toException() ?: Throwable(error?.message, error?.toException()?.cause)
                    })
            }


        suspend fun setLocation(userId: String, location: Location) {
            return suspendCoroutine { continuation ->
                REF_LOCATIONS.child(userId)
                    .child(location.id)
                    .setValue(location) { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }
        }

        suspend fun removeLocation(userId: String, locationId: String)  =
            suspendCoroutine<Unit> { continuation ->
                REF_LOCATIONS.child(userId)
                    .child(locationId)
                    .removeValue { error, _ ->
                        if(error != null )
                            throw error.toException()
                        else
                            continuation.resume(Unit)
                    }
            }
    }
}