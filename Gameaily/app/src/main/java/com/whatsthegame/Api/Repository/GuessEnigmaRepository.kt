package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.GuessAnagram
import com.whatsthegame.models.GuessEnigma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException



class GuessEnigmaRepository {
    private val apiService = RetrofitService.create()

    suspend fun guessEnigma(answer: GuessEnigma): ResponseBody {
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.guessEnigma(answer)
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
