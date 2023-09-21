package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.DeleteUserRepository
import com.whatsthegame.Api.Repository.SendPointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SendPointsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SendPointsRepository
    private val sendPointsStatus = MutableLiveData<String>()

    init {
        val context = application.applicationContext
        repository = SendPointsRepository(context)
    }

    fun sendPoints(userId: Long, points: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repository.sendPoints(userId, points)
            if (status.isNotEmpty()) {
                sendPointsStatus.postValue(status)
            }
        }
    }
}
