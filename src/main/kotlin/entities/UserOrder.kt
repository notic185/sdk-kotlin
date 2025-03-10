package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserOrder(
    var amount: Long? = null,
    var currency: String? = null,
    var order: Order? = null,
    var userOrderTransaction: UserOrderTransaction? = null
) : OwnedModel()
