package com.shoshin.models.dishes

@kotlinx.serialization.Serializable
data class DishOption (
    var id: String? = null,
    var name: String? = null,
    var variants: HashMap<String, DishOptionVariant>? = hashMapOf(),
    var isMultiCheck: Boolean = false,
    var isNecessary: Boolean = false,
    var isChecked: Boolean? = null
)