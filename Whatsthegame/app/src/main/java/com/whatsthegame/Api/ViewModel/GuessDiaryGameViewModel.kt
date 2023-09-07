package com.whatsthegame.Api.ViewModel

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.GuessDiaryGameRepository
import com.whatsthegame.models.DiaryGames
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GuessDiaryGameViewModel : ViewModel() {
    private val repository = GuessDiaryGameRepository()

    fun guessDiaryGame(choosedGame: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameGuess = repository.guessDiaryGame(GuessDiaryGame(choosedGame))

        }
    }
}
