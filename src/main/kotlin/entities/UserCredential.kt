package io.github.notic185.sdk_kotlin.entities

data class UserCredential(
    var externalId: String, var secret: String
): OwnedModel()
