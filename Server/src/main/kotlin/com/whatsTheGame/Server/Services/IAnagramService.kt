package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Anagrams

interface IAnagramService {

    fun getAll(wordName: String?): List<Anagrams?>?

    fun getAnagram(wordName: String?): Anagrams?

    fun guessTheAnagram(answer: String?): Boolean
}