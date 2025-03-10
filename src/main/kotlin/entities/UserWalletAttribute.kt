package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserWalletAttribute(
    var model: Model,
    var ownedModel: OwnedModel,
    var key: String? = null,
    var value: String? = null
) : NameLessOwnedModel()
