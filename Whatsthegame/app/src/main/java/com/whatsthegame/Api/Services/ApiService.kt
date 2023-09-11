package com.whatsthegame.Api.Services

import com.whatsthegame.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/games/diary")
    suspend fun getDiaryGame(): DiaryGames
    @GET("/games")
    suspend fun getAllNameGame(): List<AllGames>
    @POST("/games/guess")
    suspend fun guessDiaryGame(@Body request: GuessDiaryGame): ResponseBody

    @POST("/users/register")
    suspend fun registerUser(@Body request: Register): ResponseBody

    @POST("/users/login")
    suspend fun loginUser(@Body request: Login): ResponseBody
}