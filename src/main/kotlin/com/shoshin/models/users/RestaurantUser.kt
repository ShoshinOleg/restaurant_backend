package com.shoshin.models.users

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantUser (
    val id: String,
    val name: String? = null
)