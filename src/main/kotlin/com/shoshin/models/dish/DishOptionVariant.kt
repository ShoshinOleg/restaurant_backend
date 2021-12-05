package com.shoshin.domain_abstract.entities.dish

import java.io.Serializable

data class DishOptionVariant(
    var id: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var isChecked: Boolean = false
): Serializable