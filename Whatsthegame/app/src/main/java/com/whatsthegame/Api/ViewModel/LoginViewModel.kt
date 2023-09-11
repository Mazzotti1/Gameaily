package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.LoginRepository
import com.whatsthegame.Api.Repository.RegisterRepository
import com.whatsthegame.models.Login
import com.whatsthegame.models.Register
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository()
    val loginStatus = MutableLiveData<String?>()
    private var token = ""

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.loginUser(Login(email, password))
            if (result.token != null) {
                token = result.token
            }
            loginStatus.postValue(result.message)
        }
    }


    fun getToken(): String {
        return token
    }

}
