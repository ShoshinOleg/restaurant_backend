package com.shoshin.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    var id: String? = null,
    var street: String? = null,
    var house: String? = null,
    var flat: String? = null,
    var entrance: String? = null,
    var intercomCode: String? = null,
    var level: String? = null,
    var comment: String? = null,
    var coordinate: String? = null,
    var toCoordinate: Boolean = false
) {

    fun fullName() : String {
        var fullLocation = "$street, д. $house"
        if(entrance != null && entrance != "")
            fullLocation += ", подъезд $entrance"
        if(flat != null && flat != "")
            fullLocation += ", кв $flat"
        if(level != null && level != "")
            fullLocation += ", $level этаж"
        return fullLocation
    }
}