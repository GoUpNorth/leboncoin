package com.goupnorth.data.network.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Request
import org.junit.Assert.assertTrue
import org.junit.Test

class UserAgentInterceptorTest {

    @Test
    fun `when intercept add user agent header`() {
        val userAgentKey = "User-Agent"
        val userAgent = "com.goupnorth.leboncoin"
        val url = "http://www.example.com"
        val interceptor = UserAgentInterceptor(userAgent)
        val chain = mockk<okhttp3.Interceptor.Chain>(relaxed = true)
        every {
            chain.request()
        } returns Request.Builder()
            .url(url)
            .addHeader(userAgentKey, "useragent")
            .get()
            .build()

        interceptor.intercept(chain)
        verify { chain.proceed(withArg { assertTrue(it.header(userAgentKey) == userAgent) }) }
    }
}