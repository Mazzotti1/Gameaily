package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.DTOS.GamesDTO
import com.whatsTheGame.Server.Entity.Forms.GamesForm
import com.whatsTheGame.Server.Entity.Games
import com.whatsTheGame.Server.IncorrectException.RegistroIncorretoException
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
    fun getAll(): List<GamesDTO?>? {
        val games = gamesService!!.getAll()
        val gamesDTOList = games?.map { game ->
            GamesDTO(
                gameName = game?.gameName
            )
        }
        return gamesDTOList
    }
    @GetMapping("/diary")
    fun getDiaryGame(): Games? {
        return gamesService!!.getDiaryGame()
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

    @PostMapping("/setNew")
    fun setNewGames(@RequestBody form: List<GamesForm>?): ResponseEntity<out Any> {
        try {
            val newGames = gamesService!!.setNewGames(form!!)
            return ResponseEntity(newGames, HttpStatus.CREATED)
        }catch (ex: RegistroIncorretoException){
            val errorMessage = ex.message ?: "Ocorreu um erro durante a criação dos novos jogos"
            return ResponseEntity(mapOf("error" to errorMessage), HttpStatus.BAD_REQUEST)
        }
    }

}