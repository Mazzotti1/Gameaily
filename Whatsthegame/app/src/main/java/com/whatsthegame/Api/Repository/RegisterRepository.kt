package com.whatsthegame.Api.Repository

import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.Register
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.http.Body


class RegisterRepository {
    private val apiService = RetrofitService.create()

    suspend fun registerUser(request: Register): String {
        return try {
            println("Usuario: $request")
            withContext(Dispatchers.IO) {
                apiService.registerUser(request)
            }
            ""

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro HTTP: $errorBody")
            errorBody ?: "HttpException"

        } catch (e: Throwable) {
            println("Erro servidor")
            "Throwable"
        }
    }
}
