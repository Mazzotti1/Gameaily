package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.RegisterRepository
import com.whatsthegame.models.Register
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()

    fun registerUser(name: String, email: String, password:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameGuess = repository.registerUser(Register(name,email,password))

        }
    }
}