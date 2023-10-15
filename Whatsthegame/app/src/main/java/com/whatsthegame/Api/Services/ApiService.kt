package com.whatsthegame.Api.Services

import com.whatsthegame.models.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @GET("/games/diary")
    suspend fun getDiaryGame(): DiaryGames
    @GET("/games")
    suspend fun getAllNameGame(): List<AllGames>
    @POST("/games/guess/{userId}")
    suspend fun guessDiaryGame(@Body request: GuessDiaryGame, @Path("userId") userId: Long): ResponseBody

    @POST("/users/register")
    suspend fun registerUser(@Body request: Register): ResponseBody

    @POST("/users/login")
    suspend fun loginUser(@Body request: Login): ResponseBody

    @DELETE("/users/delete/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): ResponseBody

    @POST("/users/sendPoints/{userId}/{points}")
    suspend fun sendPoints(@Path("userId") userId: Long, @Path("points") points: Int): ResponseBody

    @GET("/users")
    suspend fun getAllUsers(): List<AllUsers>

    @GET("/anagrams/getAnagram")
    suspend fun getAnagrams(): Anagrams

    @POST("/anagrams/guess")
    suspend fun guessAnagram(@Body answer: GuessAnagram): ResponseBody

    @GET("/enigma/getEnigma")
    suspend fun getEnigmas(): Enigmas

    @POST("/enigma/guess")
    suspend fun guessEnigma(@Body answer: GuessEnigma): ResponseBody

    @GET("/users/rolls/{userId}")
    suspend fun getRollsByUserId(@Path("userId") userId: Long): Int

    @GET("/users/vip/{userId}")
    suspend fun getVipStatus(@Path("userId") userId: Long): Boolean

    @PATCH("/users/change/vip/{userId}")
    suspend fun setVipStatus(@Path("userId") userId: Long): ResponseBody
}