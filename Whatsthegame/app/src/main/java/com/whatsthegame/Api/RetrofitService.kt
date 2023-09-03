package com.whatsthegame.Api
import com.whatsthegame.Api.Services.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv

object RetrofitService {


    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private val baseUrl = dotenv["BASE_URL"]!!

    fun create(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}


