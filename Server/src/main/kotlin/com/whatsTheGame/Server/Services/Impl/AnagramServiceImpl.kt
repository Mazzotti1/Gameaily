package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Anagrams
import com.whatsTheGame.Server.Entity.Forms.AnagramForm
import com.whatsTheGame.Server.IncorrectException.RespostaIncorretaException
import com.whatsTheGame.Server.Repository.AnagramsRepository
import com.whatsTheGame.Server.Services.IAnagramService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class AnagramServiceImpl  @Autowired constructor(
    private val anagramsRepository: AnagramsRepository,
) : IAnagramService {

    override fun getAll(wordName: String?): List<Anagrams?>? {
        return anagramsRepository!!.findAll()
    }

    private var anagram: Anagrams? = null
    private val unguessedAnagrams: MutableList<Anagrams> = mutableListOf()
    private var consecutiveIncorrectAttempts = 0
    private val maxConsecutiveIncorrectAttempts = 3
    override fun getAnagram(wordName: String?): Anagrams? {
        if (unguessedAnagrams.isEmpty()) {
            unguessedAnagrams.addAll(anagramsRepository!!.findAll())
        }

        val random = Random.Default
        val randomIndex = random.nextInt(unguessedAnagrams.size)
        anagram = unguessedAnagrams[randomIndex]

        return anagram
    }

    @Throws(RespostaIncorretaException::class)
    override fun guessTheAnagram(answer: String?): Boolean {
        if (anagram == null) {
            return false
        }
        val isCorrect = answer == anagram?.answer
        if (isCorrect) {
            // Gere um novo anagrama se a resposta for correta.
            unguessedAnagrams.remove(anagram)
            anagram = null
            consecutiveIncorrectAttempts = 0
        } else {
            consecutiveIncorrectAttempts++
            if (consecutiveIncorrectAttempts >= maxConsecutiveIncorrectAttempts) {
                // Se o usuário errou três vezes consecutivas, mude o anagrama.
                unguessedAnagrams.remove(anagram)
                anagram = null
                consecutiveIncorrectAttempts = 0
            }
        }

        return isCorrect
    }

    override fun setNewAnagrams(forms: List<AnagramForm>?): List<Anagrams> {
        val savedAnagrams = mutableListOf<Anagrams>()

        forms?.forEach { form ->
            val anagrams = Anagrams()
            anagrams.wordName = form.wordName
            anagrams.answer = form.answer
            anagrams.difficulty = form.difficulty
            anagrams.tips = form.tips

            val savedGame = anagramsRepository.save(anagrams)
            savedAnagrams.add(savedGame)
        }

        return savedAnagrams
    }

}