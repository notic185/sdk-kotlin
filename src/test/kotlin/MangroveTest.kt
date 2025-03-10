package io.github.notic185.sdk_kotlin

import io.github.notic185.sdk_kotlin.entities.*
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.ExperimentalOkHttpApi
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.lang.reflect.Proxy
import java.net.InetAddress
import java.util.*

class MangroveTest {
    private val mangrove = Mangrove(
        endpoint = "http://10.0.0.254:4003",
        credential = UserCredential(
            externalId = "sbSO9eCvjQkE38hVnrVy4TiD",
            secret = "ER3wT3UP05TVEyh8CdMnZsCz5I1j0z",
        ),
    )

    @Test
    fun createMerchantOrder() {
        MerchantOrder(
            order = Order(
                amount = 1,
                orderCallback = OrderCallback(
                    endpoint = "http://127.0.0.1:8888"
                )
            )
        ).apply {
            this@MangroveTest.mangrove.merchantOrder.create(listOf(this)).let {
                LOG.info("The code for this order is ${it[0].order!!.code}")
            }
        }

    }

    @Test
    fun describeOrder() {
        this.mangrove.order.describe("05bf25dd-103a-4580-96b2-4e30c9736822").let {
            LOG.info("The code for this order is ${it.code}")
        }
    }

    @Test
    fun updateOrder() {
        this.mangrove.order.update(
            Order().also {
                it.uuid = "05bf25dd-103a-4580-96b2-4e30c9736822"
                it.name = Date().toString()
            }
        ).let {
            LOG.info("The name for this order is ${it[0].name}")
        }
    }

    @Test
    fun deleteOrder() {
        this.mangrove.order.delete("05bf25dd-103a-4580-96b2-4e30c9736822")
    }

    @Test
    @OptIn(ExperimentalOkHttpApi::class)
    fun handleOrderCallback() {
        MockWebServer().apply {
            this.start(inetAddress = InetAddress.getByName("0.0.0.0"), port = 8888)
        }.apply {
            while (true) {
                this.takeRequest().also {
                    // 处理回调
                    this@MangroveTest.mangrove.order.handleCallback(
                        Proxy.newProxyInstance(
                            this::class.java.classLoader,
                            arrayOf(HttpServletRequest::class.java)
                        ) { _, method, methodArguments ->
                            return@newProxyInstance when (method.name) {
                                "getMethod" -> it.method
                                "getHeaderNames" -> Collections.enumeration(it.headers.map { header -> header.first })
                                "getRequestURL" -> StringBuffer(it.requestUrl.toString())
                                "getHeader" -> it.headers[methodArguments[0] as String]
                                "getInputStream" -> it.body.inputStream().let { itBody ->
                                    object : ServletInputStream() {
                                        override fun available(): Int = itBody.available()
                                        override fun read(b: ByteArray): Int = itBody.read(b)
                                        override fun read() = throw UnsupportedOperationException()
                                        override fun isFinished() = throw UnsupportedOperationException()
                                        override fun isReady() = throw UnsupportedOperationException()
                                        override fun setReadListener(p0: ReadListener) = throw UnsupportedOperationException()
                                    }
                                }

                                else -> throw UnsupportedOperationException()
                            }
                        } as HttpServletRequest
                    )

                    // 返回响应
                    this.enqueue(
                        MockResponse().newBuilder()
                            .code(200)
                            .build()
                    )
                }
            }
        }
    }

    @Test
    fun summarizeUserIntegralAmount() {
        this.mangrove.user.summarizeIntegralAmount().forEach { key, value ->
            LOG.info("{} → {}", key, value)
        }
    }

    @Test
    fun createsUserOrder() {
        UserOrder(
            currency = "1",
            userOrderTransaction = UserOrderTransaction(
                amount = 100000,
            )
        ).apply {
            this@MangroveTest.mangrove.userOrder.create(listOf(this)).let {
                LOG.info("The code for this order is ${it[0].order!!.code}")
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MangroveTest::class.java)
    }
}
