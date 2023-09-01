package com.whatsthegame.Api


import com.whatsthegame.Api.ApiClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.whatsthegame.models.Games

class ApiManager {
    fun fetchDiaryGame(callback: (Games?) -> Unit) {
        val call = apiService.getDiaryGame()
        call.enqueue(object : Callback<Games> {
            override fun onResponse(call: Call<Games>, response: Response<Games>) {
                if (response.isSuccessful) {
                    val games = response.body()
                    callback(games) // Chame o callback com os dados obtidos
                    println(games)
                } else {
                    callback(null) // Em caso de erro, chame o callback com null
                }
            }

            override fun onFailure(call: Call<Games>, t: Throwable) {
                callback(null) // Em caso de falha, chame o callback com null
            }
        })
    }
}
