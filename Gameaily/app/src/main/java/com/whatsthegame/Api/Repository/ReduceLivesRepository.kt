package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException

class ReduceLivesRepository (private val context: Context) {
    private val apiService = RetrofitService.create(getAuthToken())
    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tokenJwt", "") ?: ""
    }

    suspend fun reduceRemaningLives(userId:Long): String {
        return try {
            withContext(Dispatchers.IO) {
                apiService.reduceRemaningLives(userId)
            }
            "Vida diminuida com sucesso!"

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro ao reduzir as vidas: $e")
            errorBody ?: "HttpException"

        } catch (e: Throwable) {
            println("Erro no servidor: $e")
            "Throwable"
        }
    }
}


