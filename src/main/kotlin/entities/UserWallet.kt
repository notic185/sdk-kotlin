package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserWallet(
    var status: String? = null,
    var type: String? = null,
    var acceptableRange: List<Int>? = null,
    var lastUsedAt: String? = null,
    var userWalletAttributes: List<UserWalletAttribute>? = null
) : OwnedModel()
