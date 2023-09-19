package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.DeleteUserRepository
import com.whatsthegame.Api.Repository.SendPointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SendPointsViewModel : ViewModel() {
    private val repository = SendPointsRepository()
    private val sendPointsStatus = MutableLiveData<String>()


    fun sendPoints(userId: Long, points: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repository.sendPoints(userId, points)
            if (status.isNotEmpty()) {
                sendPointsStatus.postValue(status)
            }
        }
    }

}