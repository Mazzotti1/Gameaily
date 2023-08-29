package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.DTOS.GamesDTO
import com.whatsTheGame.Server.Entity.Games
import com.whatsTheGame.Server.Services.Impl.GamesServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/games")
class GamesController {

    @Autowired
    private val gamesService: GamesServiceImpl? = null

    private var lastQueryTime: Instant? = null
    private var lastGameIndex: Int = 0

    @GetMapping
    fun getAll(@RequestParam(value = "gameName", required = false) gameName: String?): List<GamesDTO?>? {
        val games = gamesService!!.getAll(gameName)
        val gamesDTOList = games?.map { game ->
            GamesDTO(
                gameName = game?.gameName
            )
        }
        return gamesDTOList
    }
    @GetMapping("/diary")
    fun getDiaryGame(gameName: String?): Games? {
        return gamesService!!.getDiaryGame(gameName)
    }

    data class GuessRequest(val gameName: String)
    data class GuessResponse(val message: String)
    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun guessDiaryGame(@RequestBody request: GuessRequest): ResponseEntity<GuessResponse> {
        val isCorrect = gamesService!!.guessTheGame(request.gameName)
        if (isCorrect) {
            val responseMessage = "Resposta certa!"
            val response = GuessResponse(responseMessage)
            return ResponseEntity.ok(response)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogo errado!")

        }
    }

}