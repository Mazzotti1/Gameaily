package com.whatsthegame.Api.Repository

import android.content.Context
import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.AllGames
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
            println(e)
            emptyList()
        } catch (e: Throwable) {
            println(e)
            emptyList()
        }
    }
}
