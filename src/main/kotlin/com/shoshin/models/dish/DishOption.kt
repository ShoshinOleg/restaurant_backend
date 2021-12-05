package com.shoshin.domain_abstract.entities.dish

import java.io.Serializable

data class DishOption (
    var id: String? = null,
    var name: String? = null,
    var variants: HashMap<String, DishOptionVariant>? = hashMapOf(),
    var isMultiCheck: Boolean = false,
    var isNecessary: Boolean = false
): Serializable