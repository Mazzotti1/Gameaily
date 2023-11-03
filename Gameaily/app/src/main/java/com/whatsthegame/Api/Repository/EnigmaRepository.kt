package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.Anagrams
import com.whatsthegame.models.Enigmas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class EnigmaRepository {
    private val apiService = RetrofitService.create()

    suspend fun fetchEnigma(): Enigmas? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getEnigmas()
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