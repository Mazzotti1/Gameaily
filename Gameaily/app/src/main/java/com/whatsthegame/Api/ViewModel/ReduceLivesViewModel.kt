package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.ReduceLivesRepository
import com.whatsthegame.Api.Repository.SetUserRollsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReduceLivesViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: ReduceLivesRepository
    val ReduceLivesStatus= MutableLiveData<String>()

    init {
        val context = application.applicationContext
        repository = ReduceLivesRepository(context)
    }

    fun reduceRemaningLives(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.reduceRemaningLives(userId)
            if (response.isNotEmpty()) {
                ReduceLivesStatus.postValue(response)
            }
        }
    }
}
