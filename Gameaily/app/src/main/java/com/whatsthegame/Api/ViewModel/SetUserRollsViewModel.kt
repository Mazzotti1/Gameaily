package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.SetUserRollsRepository
import com.whatsthegame.Api.Repository.SetUserVipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetUserRollsViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: SetUserRollsRepository
    val setRollsStatus= MutableLiveData<String>()

    init {
        val context = application.applicationContext
        repository = SetUserRollsRepository(context)
    }

    fun setUserRollsUpdate(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.setUserRollsUpdate(userId)
            if (response.isNotEmpty()) {
                setRollsStatus.postValue(response)
            }
        }
    }
}




