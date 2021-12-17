package com.shoshin.models.orders

import kotlinx.serialization.Serializable

@Serializable
data class OrderMetadata(
    var id: String? = null,
    var customerId: String? = null,
    var date: String? = null,
    var status: String? = null,
    var createdAt: String? = null,
    var totalPrice: Int? = null
)