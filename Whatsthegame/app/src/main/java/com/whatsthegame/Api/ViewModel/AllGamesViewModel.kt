package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.AllGamesRepository
import com.whatsthegame.Api.Repository.DiaryGameRepository
import com.whatsthegame.models.AllGames
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllGamesViewModel : ViewModel() {
    private val repository = AllGamesRepository()
    val games = MutableLiveData<List<AllGames>>()

    fun fetchAllGames() {
        viewModelScope.launch(Dispatchers.IO) {
            val gameData = repository.getAllNameGame()
            games.postValue(gameData)
        }
    }
}
