package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.AllGames
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AllGamesRepository {
    private val apiService = RetrofitService.create()

    suspend fun getAllNameGame(): List<AllGames> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getAllNameGame()
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions here
            emptyList()
        } catch (e: Throwable) {
            emptyList()
        }
    }

}