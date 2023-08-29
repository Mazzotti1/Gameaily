package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.Entity.Anagrams
import com.whatsTheGame.Server.Services.Impl.AnagramServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/anagrams")
class AnagramController {

    @Autowired
    private val anagramService: AnagramServiceImpl? = null

    @GetMapping
    fun getAll(@RequestParam(value = "gameName", required = false) gameName: String?): List<Anagrams?>? {
        return anagramService!!.getAll(gameName)
    }

    @GetMapping("/getAnagram")
    fun getAnagram(wordName: String?): Anagrams? {
        return anagramService!!.getAnagram(wordName)
    }

    data class GuessRequest(val answer: String)
    data class GuessResponse(val message: String)
    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun guessTheAnagram(@RequestBody request: GuessRequest): ResponseEntity<GuessResponse> {
        val isCorrect = anagramService!!.guessTheAnagram(request.answer)
        if (isCorrect) {
            val responseMessage = "Resposta certa!"
            val response = GuessResponse(responseMessage)
            return ResponseEntity.ok(response)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta errada!")

        }
    }
}