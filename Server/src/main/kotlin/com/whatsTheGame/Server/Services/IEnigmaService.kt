package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Enigma

interface IEnigmaService {

    fun getAll(enigmaName: String?): List<Enigma?>?

    fun getEnigma(enigmaName: String?): Enigma?

    fun guessTheEnigma(answer: String?): Boolean
}