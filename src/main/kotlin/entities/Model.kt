package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
open class Model(
    var id: String? = null,
    var uuid: String? = null,
    var version: Long? = null,
    var deletedAt: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null
)
