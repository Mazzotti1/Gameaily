package com.whatsthegame.Api.Repository

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.whatsthegame.Api.RetrofitService
import com.whatsthegame.models.Login
import com.whatsthegame.models.Register
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class LoginResult(val token: String?, val message: String?)

class LoginRepository {
    private val apiService = RetrofitService.create()

    suspend fun loginUser(request: Login): LoginResult {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.loginUser(request)
            }
            val token = response?.string()
            LoginResult(token, "Logado com sucesso!")

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            println("Erro HTTP: $errorBody")
            LoginResult(null, errorBody ?: "HttpException")

        } catch (e: Throwable) {
            println("Erro servidor")
            LoginResult(null, "Throwable")
        }
    }

}

