package com.whatsthegame.Api

import com.whatsthegame.models.Games
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/games/diary")
    fun getDiaryGame(): Call<Games>
}