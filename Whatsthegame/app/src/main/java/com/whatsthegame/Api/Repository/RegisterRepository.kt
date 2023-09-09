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

    suspend fun registerUser(@Body request: Register): ResponseBody {
        return try {
            println("Usuario: $request")
            withContext(Dispatchers.IO) {
                apiService.registerUser(request)
            }

        } catch (e: HttpException) {
            println(e)
            return ResponseBody.create(null, "HttpExecption")

        } catch (e: Throwable) {
            println("Erro servidor")
            return ResponseBody.create(null, "Throwable")
        }
    }

}