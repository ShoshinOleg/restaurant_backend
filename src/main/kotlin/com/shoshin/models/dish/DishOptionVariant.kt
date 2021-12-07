package com.shoshin.models.dish

import kotlinx.serialization.Serializable

@Serializable
data class DishOptionVariant(
    var id: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var isChecked: Boolean = false
)