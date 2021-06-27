package com.cpma.app.utils.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor() : Interceptor {
    private var authToken: String? = null

    fun setAuthToken(authToken: String) {
        this.authToken = authToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if(authToken != null)
                builder.header("Authorization", authToken!!)

        return chain.proceed(builder.build())
    }

}