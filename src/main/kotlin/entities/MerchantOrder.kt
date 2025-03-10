package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class MerchantOrder(
    var merchant: User? = null,
    var order: Order? = null,
    var externalIPForCreator: String? = null,
    var externalIPForPayer: String? = null
): OwnedModel()
