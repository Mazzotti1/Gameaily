package com.whatsthegame.Api.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsthegame.Api.Repository.AnagramsRepository
import com.whatsthegame.Api.Repository.EnigmaRepository
import com.whatsthegame.models.Anagrams
import com.whatsthegame.models.Enigmas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EnigmasViewModel : ViewModel() {
    private val repository = EnigmaRepository()
    val enigma = MutableLiveData<Enigmas?>()

    fun fetchEnigmaGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val anagramData = repository.fetchEnigma()
            enigma.postValue(anagramData)
        }
    }
}