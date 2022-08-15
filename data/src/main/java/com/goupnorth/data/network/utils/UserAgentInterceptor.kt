package com.goupnorth.data.network.utils

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Set the user-agent in all http requests
 */
class UserAgentInterceptor(private val userAgent: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header(USER_AGENT_KEY, userAgent)
            .build()

        return chain.proceed(request)
    }

    companion object {
        private const val USER_AGENT_KEY = "User-Agent"
    }
}