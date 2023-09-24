package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException

class GuessDiaryGameRepository {
    private val apiService = RetrofitService.create()

    suspend fun guessDiaryGame(request: GuessDiaryGame, userId: Long): ResponseBody {
        return try {
            withContext(Dispatchers.IO) {
                apiService.guessDiaryGame(request, userId)
            }
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
