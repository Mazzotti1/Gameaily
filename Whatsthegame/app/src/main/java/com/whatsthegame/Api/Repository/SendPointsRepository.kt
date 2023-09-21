package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SendPointsRepository(private val context: Context) {
    private val apiService = RetrofitService.create(getAuthToken())

    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tokenJwt", "") ?: ""
    }

    suspend fun sendPoints(userId: Long, points: Int): String {
        return try {
            withContext(Dispatchers.IO) {
                apiService.sendPoints(userId, points)
            }
            "Pontos adcionados com sucesso!"

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro HTTP: $errorBody")
            errorBody ?: "HttpException"

        } catch (e: Throwable) {
            println("Erro no servidor: $e")
            "Throwable"
        }
    }
}