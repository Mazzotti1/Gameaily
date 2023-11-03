package com.whatsthegame.Api.ViewModel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.SendPointsRepository
import com.whatsthegame.Api.Repository.SetUserVipRepository
import okhttp3.ResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetUserVipViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SetUserVipRepository
    val setVipStatus = MutableLiveData<String>()

    init {
        val context = application.applicationContext
        repository = SetUserVipRepository(context)
    }


    fun setVipStatus(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
                val response = repository.setVipStatus(userId)
                if (response.isNotEmpty()) {
                    setVipStatus.postValue(response)
                }
        }
    }
}
