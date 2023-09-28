package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.AllGames
import com.whatsthegame.models.AllUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AllUsersRepository {
    private val apiService = RetrofitService.create()

    suspend fun getAllUsers(): List<AllUsers>{
        return  try{
            withContext(Dispatchers.IO){
                apiService.getAllUsers()

            }
        }catch (e:HttpException){
            println(e)
            emptyList()
        }catch (e:Throwable){
            println(e)
            emptyList()
        }
    }
}

