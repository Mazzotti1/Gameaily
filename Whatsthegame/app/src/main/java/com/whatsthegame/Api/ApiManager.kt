package com.whatsthegame.Api


import com.whatsthegame.Api.ApiClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.whatsthegame.models.Games

class ApiManager {
    fun fetchDiaryGame(param: (Any) -> Unit) {
        val call = apiService.getDiaryGame()
        call.enqueue(object : Callback<Games> {
            override fun onResponse(call: Call<Games>, response: Response<Games>) {
                if (response.isSuccessful) {
                    val games = response.body()
                    println(games)
                } else {
                    println("Erro 1")
                }
            }
            override fun onFailure(call: Call<Games>, t: Throwable) {
                println("Erro 2")
            }
        })
    }
}
