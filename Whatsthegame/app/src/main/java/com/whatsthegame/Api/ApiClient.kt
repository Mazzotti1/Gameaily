package com.whatsthegame.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv

object ApiClient {


    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private val baseUrl = dotenv["BASE_URL"]!!
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)!!
}
