package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Anagrams
import com.whatsTheGame.Server.Entity.Forms.AnagramForm

interface IAnagramService {

    fun getAll(wordName: String?): List<Anagrams?>?

    fun getAnagram(wordName: String?): Anagrams?

    fun guessTheAnagram(answer: String?): Boolean

    fun setNewAnagrams(forms: List<AnagramForm>?): List<Anagrams>
}