package com.example.esp_p2p.data.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class DynamicHostInterceptor(
    var hostname: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (hostname != null){
            val newUrl = request.url.newBuilder().host(hostname!!).build()
            request = request.newBuilder().url(newUrl).build()
        }
        return chain.proceed(request)
    }

}