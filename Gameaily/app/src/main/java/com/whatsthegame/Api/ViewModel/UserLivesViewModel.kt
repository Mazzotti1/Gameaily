package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.UserLivesRepository
import com.whatsthegame.Api.Repository.UserRollsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserLivesViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: UserLivesRepository = UserLivesRepository(application.applicationContext)

    private val _lives = MutableLiveData<Int?>()
    val lives: LiveData<Int?> = _lives

    fun getUserLives(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getRemainingLivesStatus(userId)
                _lives.postValue(result)
            } catch (e: HttpException) {
                e.printStackTrace()
                _lives.postValue(null)
            } catch (e: Throwable) {
                e.printStackTrace()
                _lives.postValue(null)
            }
        }
    }

}
