package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
open class NamedModel(
    var name: String? = null,
    var description: String? = null
): Model()
