package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.UserVipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserVipViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: UserVipRepository = UserVipRepository(application.applicationContext)

    private val _vip = MutableLiveData<Boolean?>()
    val vip: LiveData<Boolean?> = _vip

    fun getVip(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.fetchVip(userId)
                _vip.postValue(result)
            } catch (e: HttpException) {
                e.printStackTrace()
                _vip.postValue(null)
            } catch (e: Throwable) {
                e.printStackTrace()
                _vip.postValue(null)
            }
        }
    }

}

