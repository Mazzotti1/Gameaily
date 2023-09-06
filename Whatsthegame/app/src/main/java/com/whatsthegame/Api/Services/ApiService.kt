package com.whatsthegame.Api.Services

import com.whatsthegame.models.AllGames
import com.whatsthegame.models.DiaryGames
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/games/diary")
    suspend fun getDiaryGame(): DiaryGames
    @GET("/games")
    suspend fun getAllNameGame(): List<AllGames>

}