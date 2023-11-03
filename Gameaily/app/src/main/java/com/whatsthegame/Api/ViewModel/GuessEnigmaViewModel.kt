package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.GuessAnagramRepository
import com.whatsthegame.Api.Repository.GuessEnigmaRepository
import com.whatsthegame.models.GuessAnagram
import com.whatsthegame.models.GuessEnigma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuessEnigmaViewModel : ViewModel() {
    private val repository = GuessEnigmaRepository()

    fun guessEnigma(answer: GuessEnigma) {
        viewModelScope.launch(Dispatchers.IO) {
            val enigmaGuess = repository.guessEnigma(answer)
        }
    }
}