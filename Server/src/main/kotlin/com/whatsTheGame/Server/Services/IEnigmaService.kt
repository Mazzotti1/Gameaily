package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Enigma
import com.whatsTheGame.Server.Entity.Forms.EnigmaForm

interface IEnigmaService {

    fun getAll(enigmaName: String?): List<Enigma?>?

    fun getEnigma(enigmaName: String?): Enigma?

    fun guessTheEnigma(answer: String?): Boolean

    fun setNewEnigmas(forms: List<EnigmaForm>?): List<Enigma>
}