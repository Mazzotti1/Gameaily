package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.Anagrams
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AnagramsRepository {
    private val apiService = RetrofitService.create()

    suspend fun fetchAnagram(): Anagrams? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getAnagrams()
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions here
            println("Erro no anagrama: $e")
            null
        } catch (e: Throwable) {
            println("Erro no anagrama: $e")
            null
        }
    }
}