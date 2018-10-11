package com.github.repos.data.remote

import com.github.repos.BuildConfig
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.google.gson.GsonBuilder


class ServiceGenerator {

    private var httpClient = OkHttpClient.Builder()
    var builder = Retrofit.Builder()

    // Final Calling Method
    fun <S> createService(baseURL: String,
                                  serviceClass: Class<S>): S {

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(logging)
        }

        val gson = GsonBuilder()
                .setLenient()
                .create()

        builder.baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        var interceptor: Interceptor

            interceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Charset", "UTF-8")
                        .build()
                chain.proceed(request)
            }


        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor)

            builder.client(httpClient.build())
        }
        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }
}