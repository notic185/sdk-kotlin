package io.github.notic185.sdk_kotlin.entities

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializer(String::class)
@OptIn(ExperimentalSerializationApi::class)
internal object OrderCallbackEndpointSerializer : KSerializer<String> {
    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString("v2:$value")
    }

    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeString()
    }
}

@Serializable
data class OrderCallback(
    @Serializable(OrderCallbackEndpointSerializer::class)
    var endpoint: String? = null
) : Model()
