package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.GuessAnagram
import com.whatsthegame.models.GuessDiaryGame
import com.whatsthegame.models.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException

class GuessAnagramRepository {
    private val apiService = RetrofitService.create()

    suspend fun guessAnagram(answer: GuessAnagram): ResponseBody {
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.guessAnagram(answer)
            }
            println("Resposta correta")
            return response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown Error"
            println("HTTP Error: ${e.code()}, $errorBody")
            return ResponseBody.create(null, errorBody)
        } catch (e: Throwable) {
            println("Server Error: $e")
            return ResponseBody.create(null, "Server Error: $e")
        }
    }
}
