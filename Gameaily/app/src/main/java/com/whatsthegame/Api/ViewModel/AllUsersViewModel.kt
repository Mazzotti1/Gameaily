package com.whatsthegame.Api.ViewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.whatsthegame.Api.Repository.AllGamesRepository
import com.whatsthegame.Api.Repository.AllUsersRepository
import com.whatsthegame.models.AllGames
import com.whatsthegame.models.AllUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllUsersViewModel : ViewModel(){
    private val repository = AllUsersRepository()
    val users = MutableLiveData<List<AllUsers>>()

    fun fetchAllUsers(){
        viewModelScope.launch(Dispatchers.IO){
            val usersData = repository.getAllUsers()
            users.postValue(usersData)
        }
    }
}

