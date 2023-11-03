package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SetUserRollsRepository (private val context: Context) {
    private val apiService = RetrofitService.create(getAuthToken())
    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tokenJwt", "") ?: ""
    }
    suspend fun setUserRollsUpdate(userId:Long): String {
        return try {
            withContext(Dispatchers.IO) {
                apiService.setRollsUpdate(userId)
            }
            "Pontos adcionados com sucesso!"

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro ao enviar os pontos: $e")
            errorBody ?: "HttpException"

        } catch (e: Throwable) {
            println("Erro no servidor: $e")
            "Throwable"
        }
    }
}

