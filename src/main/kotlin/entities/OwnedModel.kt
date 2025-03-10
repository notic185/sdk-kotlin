package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
open class OwnedModel(
    var user: User? = null,
    var owner: User? = null
): NamedModel()
