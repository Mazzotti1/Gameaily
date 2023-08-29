package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.Entity.Enigma
import com.whatsTheGame.Server.Services.Impl.EnigmaServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/enigma")
class EnigmaController {

    @Autowired
    private val enigmaService: EnigmaServiceImpl? = null

    @GetMapping
    fun getAll(@RequestParam(value = "enigmaName", required = false) enigmaName: String?): List<Enigma?>? {
        return enigmaService!!.getAll(enigmaName)
    }

    @GetMapping("/getEnigma")
    fun getEnigma(tipName: String?): Enigma? {
        return enigmaService!!.getEnigma(tipName)
    }

    data class GuessRequest(val answer: String)
    data class GuessResponse(val message: String)
    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun guessTheEnigma(@RequestBody request: GuessRequest): ResponseEntity<GuessResponse> {
        val isCorrect = enigmaService!!.guessTheEnigma(request.answer)
        if (isCorrect) {
            val responseMessage = "Resposta certa!"
            val response = GuessResponse(responseMessage)
            return ResponseEntity.ok(response)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta errada!")

        }
    }
}