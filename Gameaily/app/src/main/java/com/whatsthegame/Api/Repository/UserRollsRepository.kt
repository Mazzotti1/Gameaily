package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.Enigmas
import com.whatsthegame.models.Rolls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class UserRollsRepository (private val context: Context) {
    private val apiService = RetrofitService.create(getAuthToken())

    private fun getAuthToken(): String {
        val sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tokenJwt", "") ?: ""
    }

    suspend fun fetchRolls(userId:Long): Int? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getRollsByUserId(userId)
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
