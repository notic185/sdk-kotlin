package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.Serializable

@Serializable
data class Result<T>(
    var code: Int? = null,
    var message: String? = null,
    var data: T? = null,
)
