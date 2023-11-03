package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class DeleteUserRepository {
    private val apiService = RetrofitService.create()

    suspend fun deleteUser(userId: Long): String {
        return try {

            withContext(Dispatchers.IO) {
                apiService.deleteUser(userId)
            }
            "Conta deletada com sucesso!"

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro HTTP: $errorBody")
            errorBody ?: "HttpException"

        } catch (e: Throwable) {
            println("Erro no servidor: $e")
            "Throwable"
        }
    }
}