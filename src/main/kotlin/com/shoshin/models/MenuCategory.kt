package com.shoshin.models

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategory (
    var id: String? = null,
    var name: String? = null,
    var imageURL: String? = null,
    var itemsIds: HashMap<String, String>? = null
)