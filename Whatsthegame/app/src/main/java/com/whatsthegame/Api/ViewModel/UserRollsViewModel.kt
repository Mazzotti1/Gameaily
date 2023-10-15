package com.whatsthegame.Api.ViewModel

import android.app.Application
import androidx.lifecycle.*
import com.whatsthegame.Api.Repository.EnigmaRepository
import com.whatsthegame.Api.Repository.SendPointsRepository
import com.whatsthegame.Api.Repository.UserRollsRepository
import com.whatsthegame.models.Enigmas
import com.whatsthegame.models.Rolls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserRollsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRollsRepository = UserRollsRepository(application.applicationContext)

    private val _rolls = MutableLiveData<Int?>()
    val rolls: LiveData<Int?> = _rolls

    fun getRolls(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.fetchRolls(userId)
                _rolls.postValue(result)
            } catch (e: HttpException) {
                e.printStackTrace()
                _rolls.postValue(null)
            } catch (e: Throwable) {
                e.printStackTrace()
                _rolls.postValue(null)
            }
        }
    }
}
