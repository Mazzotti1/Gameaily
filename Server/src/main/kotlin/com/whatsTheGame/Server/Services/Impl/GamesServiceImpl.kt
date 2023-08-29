package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Games
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

    override fun getAll(gameName: String?): List<Games?>? {
        return gamesRepository!!.findAll()
    }

    private var gameOfTheDay: Games? = null

    override fun getDiaryGame(gameName: String?): Games? {
        val allGames = gamesRepository!!.findAll()

        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val random = Random(dayOfYear)

        val randomIndex = random.nextInt(allGames.size)
        gameOfTheDay = allGames[randomIndex]
        return gameOfTheDay
    }

    override fun guessTheGame(gameName: String?): Boolean {
        if (gameOfTheDay == null) {
            return false
        }
        return gameName == gameOfTheDay?.gameName
    }


}



