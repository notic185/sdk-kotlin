package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderTransaction(
    var amount: Long? = null,
    var blockId: String? = null,
    var fromAddress: String? = null,
    var toAddress: String? = null,
) : OwnedModel()
