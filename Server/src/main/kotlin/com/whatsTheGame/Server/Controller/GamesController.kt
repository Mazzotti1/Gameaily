package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.DTOS.GamesDTO
import com.whatsTheGame.Server.Services.Impl.GamesServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
class GamesController {

    @Autowired
    private val gamesService: GamesServiceImpl? = null

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

}