package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.DeleteUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DeleteUserViewModel : ViewModel() {
    private val repository = DeleteUserRepository()
    val deleteStatus = MutableLiveData<String>()


    fun deleteUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repository.deleteUser(userId)
            if (status.isNotEmpty()) {
                deleteStatus.postValue(status)
            }
        }
    }

}
