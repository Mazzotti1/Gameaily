package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Forms.GamesForm
import com.whatsTheGame.Server.Entity.Games
import com.whatsTheGame.Server.IncorrectException.RespostaIncorretaException
import com.whatsTheGame.Server.Repository.GamesRepository
import com.whatsTheGame.Server.Repository.UserRepository
import com.whatsTheGame.Server.Services.IGamesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class GamesServiceImpl @Autowired constructor(
    private val gamesRepository: GamesRepository,
    private val userRepository: UserRepository,
) : IGamesService {

    override fun getAll(): List<Games?>? {
        return gamesRepository!!.findAll()
    }

    private var gameOfTheDay: Games? = null

    override fun getDiaryGame(): Pair<Games?, String> {
        val allGames = gamesRepository!!.findAll()

        val calendar = Calendar.getInstance()
        val currentMillis = calendar.timeInMillis

        // Define a data para o próximo dia
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val nextDayMillis = calendar.timeInMillis

        // Calcula o tempo restante em milissegundos
        val millisUntilNextDay = nextDayMillis - currentMillis

        // Converte o tempo restante em horas, minutos e segundos
        val hoursUntilNextDay = millisUntilNextDay / (60 * 60 * 1000)
        val minutesUntilNextDay = (millisUntilNextDay % (60 * 60 * 1000)) / (60 * 1000)
        val secondsUntilNextDay = (millisUntilNextDay % (60 * 1000)) / 1000

        val random = Random(nextDayMillis)
        val randomIndex = random.nextInt(allGames.size)
         gameOfTheDay = allGames[randomIndex]

        val timer = String.format("%02d:%02d:%02d", hoursUntilNextDay, minutesUntilNextDay, secondsUntilNextDay)


        return Pair(gameOfTheDay, timer)
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




