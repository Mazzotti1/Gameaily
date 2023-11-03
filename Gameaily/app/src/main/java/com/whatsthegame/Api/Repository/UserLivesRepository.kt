package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserLivesRepository (private val context: Context) {
    private val apiService = RetrofitService.create(getAuthToken())

    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tokenJwt", "") ?: ""

    }
    suspend fun getRemainingLivesStatus(userId:Long): Int? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getRemainingLivesStatus(userId)
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions here
            println("Erro no Enigma: $e")
            null
        } catch (e: Throwable) {
            println("Erro no Enigma: $e")
            null
        }
    }
}


