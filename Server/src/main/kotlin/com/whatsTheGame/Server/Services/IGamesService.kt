package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Forms.GamesForm
import com.whatsTheGame.Server.Entity.Games

interface IGamesService {

    fun getAll(gameName: String?): List<Games?>?

    fun getDiaryGame(): Games?

    fun guessTheGame(gameName: String?): Boolean

    fun setNewGames(forms: List<GamesForm>?): List<Games>
}