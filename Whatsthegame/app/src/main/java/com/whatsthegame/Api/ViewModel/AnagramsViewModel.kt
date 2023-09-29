package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.AnagramsRepository
import com.whatsthegame.Api.Repository.DiaryGameRepository
import com.whatsthegame.models.Anagrams
import com.whatsthegame.models.DiaryGames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnagramsViewModel : ViewModel() {
    private val repository = AnagramsRepository()
    val anagram = MutableLiveData<Anagrams?>()

    fun fetchDiaryGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val anagramData = repository.fetchAnagram()
            anagram.postValue(anagramData)
        }
    }
}