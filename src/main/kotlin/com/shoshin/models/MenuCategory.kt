package com.shoshin.models

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategory (
    var id: String? = null,
    var name: String? = null,
    var imageURL: String? = null,
    var dishesIds: HashMap<String, String>? = null
)