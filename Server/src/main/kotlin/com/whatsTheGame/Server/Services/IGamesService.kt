package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Games

interface IGamesService {

    fun getAll(gameName: String?): List<Games?>?

    fun getDiaryGame(gameName: String?): Games?

    fun guessTheGame(gameName: String?): Boolean

}