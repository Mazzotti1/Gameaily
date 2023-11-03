package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.RegisterRepository
import com.whatsthegame.models.Register
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()
    val registerStatus = MutableLiveData<String>()


    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repository.registerUser(Register(name, email, password))
            if (status.isNotEmpty()) {
                registerStatus.postValue(status)
            }
        }
    }
}
