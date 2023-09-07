package com.whatsthegame.Api.Services

import com.whatsthegame.Api.ViewModel.GuessDiaryGameViewModel
import com.whatsthegame.models.AllGames
import com.whatsthegame.models.DiaryGames
import com.whatsthegame.models.GuessDiaryGame
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
}