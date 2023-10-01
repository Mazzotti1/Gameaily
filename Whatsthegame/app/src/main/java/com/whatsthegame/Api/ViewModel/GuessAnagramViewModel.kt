package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.GuessAnagramRepository
import com.whatsthegame.Api.Repository.GuessDiaryGameRepository
import com.whatsthegame.models.GuessAnagram
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuessAnagramViewModel : ViewModel() {
    private val repository = GuessAnagramRepository()

    fun guessAnagram(answer: GuessAnagram) {
        viewModelScope.launch(Dispatchers.IO) {
            val anagramGuess = repository.guessAnagram(answer)
        }
    }
}