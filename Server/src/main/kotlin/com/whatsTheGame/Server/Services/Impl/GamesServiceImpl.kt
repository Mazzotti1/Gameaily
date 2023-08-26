package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Games
import com.whatsTheGame.Server.Repository.GamesRepository
import com.whatsTheGame.Server.Services.IGamesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GamesServiceImpl @Autowired constructor(
    private val gamesRepository: GamesRepository,
) : IGamesService {

    override fun getAll(gameName: String?): List<Games?>? {
        return gamesRepository!!.findAll()
    }



}