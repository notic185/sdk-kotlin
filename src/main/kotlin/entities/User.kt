package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var type: String? = null,
    var logStatus: String? = null,
    var integral: Long? = null,
    var lastSeenAt: String? = null
): NamedModel()
