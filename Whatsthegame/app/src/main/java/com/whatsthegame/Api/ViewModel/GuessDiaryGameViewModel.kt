package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.GuessDiaryGameRepository
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GuessDiaryGameViewModel : ViewModel() {
    private val repository = GuessDiaryGameRepository()

    fun guessDiaryGame(request: GuessDiaryGame, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameGuess = repository.guessDiaryGame(request, userId)
        }
    }
}