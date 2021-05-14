package com.example.imlotterytool.network

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 *
 */
object RetrofitManager {

    const val JUHEBASEURL = "http://apis.juhe.cn"

    fun <T> createService(retrofit: Retrofit = juheRetrofit, clazz: Class<T>): T {

        return retrofit.create(clazz)
    }


    private val juheRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(JUHEBASEURL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}
