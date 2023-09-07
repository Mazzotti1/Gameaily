package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.http.Body


class GuessDiaryGameRepository {
    private val apiService = RetrofitService.create()

    suspend fun guessDiaryGame(@Body request: GuessDiaryGame): ResponseBody {
        return try {
            println("Jogo: $request")
            withContext(Dispatchers.IO) {
                apiService.guessDiaryGame(request)
            }

        } catch (e: HttpException) {
            println("Jogo errado")
            return ResponseBody.create(null, "HttpExecption")

        } catch (e: Throwable) {
            println("Erro servidor")
            return ResponseBody.create(null, "Throwable")
        }
    }
}
