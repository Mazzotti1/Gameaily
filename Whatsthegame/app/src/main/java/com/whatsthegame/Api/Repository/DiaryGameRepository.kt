package com.whatsthegame.Api.Repository
import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DiaryGameRepository {
    private val apiService = RetrofitService.create()

    suspend fun fetchDiaryGame(): DiaryGames? {
        return try {
            withContext(Dispatchers.IO) {
                apiService.getDiaryGame()
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions here
            null
        } catch (e: Throwable) {
            // Handle other exceptions here
            null
        }
    }
}
