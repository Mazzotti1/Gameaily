package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.DiaryGameRepository
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val repository = DiaryGameRepository()
    val game = MutableLiveData<DiaryGames?>()

    fun fetchDiaryGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val gameData = repository.fetchDiaryGame()
            game.postValue(gameData)
        }
    }
}
