package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    var orderTransactionId: String? = null,
    var status: String? = null,
    var amount: Long? = null,
    var integralAmount: Long? = null,
    var externalId: String? = null,
    var code: String? = null,
    var orderCallback: OrderCallback? = null,
    val userOrder: UserOrder? = null,
    val merchantOrder: MerchantOrder? = null,
) : OwnedModel()
