package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Forms.GamesForm
import com.whatsTheGame.Server.Entity.Games

interface IGamesService {

    fun getAll(): List<Games?>?

    fun getDiaryGame(): Pair<Games?, String?>

    fun guessTheGame(gameName: String?): Boolean

    fun setNewGames(forms: List<GamesForm>?): List<Games>
}