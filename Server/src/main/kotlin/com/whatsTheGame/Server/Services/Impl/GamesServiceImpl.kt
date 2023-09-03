package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Forms.GamesForm
import com.whatsTheGame.Server.Entity.Games
import com.whatsTheGame.Server.IncorrectException.RespostaIncorretaException
import com.whatsTheGame.Server.Repository.GamesRepository
import com.whatsTheGame.Server.Services.IGamesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class GamesServiceImpl @Autowired constructor(
    private val gamesRepository: GamesRepository,
) : IGamesService {

    override fun getAll(): List<Games?>? {
        return gamesRepository!!.findAll()
    }

    private var gameOfTheDay: Games? = null

    override fun getDiaryGame(): Games? {
        val allGames = gamesRepository!!.findAll()

        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val random = Random(dayOfYear)

        val randomIndex = random.nextInt(allGames.size)
        gameOfTheDay = allGames[randomIndex]
        return gameOfTheDay
    }

    @Throws(RespostaIncorretaException::class)
    override fun guessTheGame(gameName: String?): Boolean {
        if (gameOfTheDay == null) {
            return false
        }
        return gameName == gameOfTheDay?.gameName
    }

    override fun setNewGames(forms: List<GamesForm>?): List<Games> {
        val savedGames = mutableListOf<Games>()

        forms?.forEach { form ->
            val games = Games()
            games.gameName = form.gameName
            games.gameImage = form.gameImage
            games.difficulty = form.difficulty
            games.tips = form.tips

            val savedGame = gamesRepository.save(games)
            savedGames.add(savedGame)
        }

        return savedGames
    }

}




