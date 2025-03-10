package io.github.notic185.sdk_kotlin

import io.github.notic185.sdk_kotlin.entities.*
import io.github.notic185.sdk_kotlin.utilities.Simplifier
import jakarta.servlet.http.HttpServletRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Mangrove0(val endpoint: String, val credential: UserCredential) {
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                // -
                val request = chain.request()
                    .newBuilder()
                    .also {
                        it.header("Authorization", "Signature ${this@Mangrove0.signRequest(it)}")
                    }
                    .build()
                // 继续请求
                return chain.proceed(request)
            }
        })
        .build()
    val contentOperator = Json { ignoreUnknownKeys = true }

    inline fun <reified R> request(requestMethod: String, requestPath: String): R {
        return this.request<R, Unit>(requestMethod, requestPath, null)
    }

    inline fun <reified R, reified A> request(
        requestMethod: String,
        requestPath: String,
        requestPayload: A? = null
    ): R {
        // 构建请求
        // > 请求体类型
        val requestPayloadType = "application/json; charset=utf-8".toMediaType()
        // > 请求路径
        val requestURL = "${this.endpoint}$requestPath".toHttpUrl()
        // > -
        val request = Request.Builder()
            .url(requestURL)
            .method(
                requestMethod,
                if (requestPayload == null) {
                    null
                } else {
                    Json.encodeToString<A>(requestPayload).toRequestBody(requestPayloadType)
                }
            )
            .header("content-type", requestPayloadType.toString())
            .header("host", requestURL.host)
            .header("x-guarder-id", this.credential.externalId)
            .header("x-guarder-signed-at", System.currentTimeMillis().toString())
            .header("x-guarder-uuid", UUID.randomUUID().toString())
            .build()
        // 发送请求
        this.httpClient.newCall(request).execute().use {
            if (it.headers("Content-Type").any { it.contains("application/json") }) {
                // -
                val responsePayload = this.contentOperator.decodeFromString<Result<R>>(it.body.string())
                // -
                if ((responsePayload.code ?: 500) and 200 == 200) {
                    return responsePayload.data!!
                } else {
                    throw RuntimeException(responsePayload.message)
                }
            } else {
                if (it.isSuccessful) {
                    throw RuntimeException("Unexpected content type ${it.headers("Content-Type")}")
                } else {
                    throw RuntimeException("Unexpected status code ${it.code}")
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    internal fun signRequest(requestBuilder: Request.Builder): String {
        // 初始化上下文
        // > 构建请求
        val request = requestBuilder.build()
        // > 构建请求签名
        val requestSignature = mutableListOf(
            "${request.method} ${request.url.encodedPath}",
        ).also {
            arrayOf("content-type", "host", "x-guarder-id", "x-guarder-signed-at", "x-guarder-uuid").forEach { headerName ->
                it.add("${headerName}: ${request.header(headerName)}")
            }
        }.also {
            it.add("")
        }.also {
            if (request.body == null) {
                it.add("")
            } else {
                Simplifier.pileDown(
                    Json.decodeFromString<JsonElement>(
                        Buffer().also {
                            request.body!!.writeTo(it)
                        }.readUtf8()
                    )
                ).let { requestBody ->
                    it.add(
                        requestBody.keys.sorted().joinToString("&") { key ->
                            "$key=${(requestBody[key] as JsonPrimitive).content}"
                        }
                    )
                }
            }
        }

        // 签名并返回
        Mac.getInstance("HmacSHA512").also {
            it.init(
                SecretKeySpec(
                    this.credential.secret.toByteArray(), "HmacSHA512"
                )
            )
        }.let {
            return it.doFinal(
                requestSignature.joinToString("\r\n").toByteArray()
            ).toHexString()
        }
    }
}

class Mangrove(private val endpoint: String, private val credential: UserCredential) {
    class MangroveMerchantOrder(private val mangrove: Mangrove) {
        fun create(merchantOrders: List<MerchantOrder>): List<MerchantOrder> {
            return this.mangrove.`0`.request("PUT", "/v1.2/merchant-order", merchantOrders)
        }
    }

    class MangroveOrder(private val mangrove: Mangrove) {
        fun describe(uuid: String): Order {
            return this.mangrove.`0`.request("GET", "/v1.2/order/$uuid")
        }

        fun delete(uuid: String): Order {
            return this.mangrove.`0`.request("DELETE", "/v1.2/order/$uuid")
        }

        fun update(order: Order): List<Order> {
            return this.mangrove.`0`.request("PATCH", "/v1.2/order/${order.uuid}", order)
        }

        fun handleCallback(request: HttpServletRequest): Order {
            // 初始化上下文
            val requestBody = request.inputStream.readBytes()

            // 构建签名
            val requestSignature = this.mangrove.`0`.signRequest(
                Request.Builder()
                    .url(request.requestURL.toString())
                    .also {
                        request.headerNames.toList().map { headerName ->
                            it.header(headerName, request.getHeader(headerName))
                        }
                    }
                    .method(
                        request.method,
                        if (requestBody.isEmpty()) {
                            null
                        } else {
                            requestBody.toRequestBody(
                                request.getHeader("Content-Type").toMediaType()
                            )
                        }
                    )
            )

            // 验证签名并返回
            if (request.getHeader("Authorization").split(" ")[1] == requestSignature) {
                return this.mangrove.`0`.contentOperator.decodeFromString(String(requestBody))
            } else {
                throw IllegalArgumentException("Invalid signature")
            }
        }
    }

    class MangroveUser(private val mangrove: Mangrove) {
        fun summarizeIntegralAmount(): Map<String, Float> {
            return this.mangrove.`0`.request("GET", "/v1.2/user/summarize-integral-amount")
        }
    }

    class MangroveUserOrder(private val mangrove: Mangrove) {
        fun create(userOrders: List<UserOrder>): List<UserOrder> {
            return this.mangrove.`0`.request("PUT", "/v1.2/user-order", userOrders)
        }
    }

    private val `0` = Mangrove0(this.endpoint, this.credential)
    val merchantOrder = MangroveMerchantOrder(this)
    val order = MangroveOrder(this)
    val user = MangroveUser(this)
    val userOrder = MangroveUserOrder(this)
}
