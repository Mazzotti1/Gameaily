package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Enigma
import com.whatsTheGame.Server.IncorrectException.RespostaIncorretaException
import com.whatsTheGame.Server.Repository.EnigmaRepository
import com.whatsTheGame.Server.Services.IEnigmaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EnigmaServiceImpl  @Autowired constructor(
    private val pistasRepository: EnigmaRepository,
) : IEnigmaService {

    override fun getAll(enigmaName: String?): List<Enigma?>? {
        return pistasRepository!!.findAll()
    }

    private var enigma: Enigma? = null
    private val unguessedAnagrams: MutableList<Enigma> = mutableListOf()
    private var consecutiveIncorrectAttempts = 0
    private val maxConsecutiveIncorrectAttempts = 3
    override fun getEnigma(enigmaName: String?): Enigma? {
        if (unguessedAnagrams.isEmpty()) {
            unguessedAnagrams.addAll(pistasRepository!!.findAll())
        }

        val random = Random.Default
        val randomIndex = random.nextInt(unguessedAnagrams.size)
        enigma = unguessedAnagrams[randomIndex]

        return enigma
    }

    @Throws(RespostaIncorretaException::class)
    override fun guessTheEnigma(answer: String?): Boolean {
        if (enigma == null) {
            return false
        }
        val isCorrect = answer == enigma?.answer
        if (isCorrect) {

            unguessedAnagrams.remove(enigma)
            enigma = null
            consecutiveIncorrectAttempts = 0
        } else {
            consecutiveIncorrectAttempts++
            if (consecutiveIncorrectAttempts >= maxConsecutiveIncorrectAttempts) {

                unguessedAnagrams.remove(enigma)
                enigma = null
                consecutiveIncorrectAttempts = 0
            }
        }
        return isCorrect
    }


}