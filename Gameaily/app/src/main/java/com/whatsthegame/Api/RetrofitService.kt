package com.whatsthegame.Api

import AuthInterceptor
import android.content.Context
import com.whatsthegame.Api.Services.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv

import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext


object RetrofitService {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private val baseUrl = dotenv["BASE_URL"]!!

    fun create(authtoken:String?= ""): ApiService {


        val okHttpClient = authtoken?.let { AuthInterceptor(it) }?.let {
            OkHttpClient.Builder()
                .addInterceptor(it)
                .build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

}

