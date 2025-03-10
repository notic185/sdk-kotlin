package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
open class NameLessOwnedModel(
    var user: User? = null,
    var owner: User? = null
): Model()
