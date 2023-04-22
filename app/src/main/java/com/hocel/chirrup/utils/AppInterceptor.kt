package com.hocel.chirrup.utils

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInterceptor @Inject constructor() : Interceptor {
    private var openaiAPIKey: String? = null
    fun setOpenaiAPIKey(accessToken: String?) {
        this.openaiAPIKey = accessToken
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestBuilder: Request.Builder = request.newBuilder()
        openaiAPIKey?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}