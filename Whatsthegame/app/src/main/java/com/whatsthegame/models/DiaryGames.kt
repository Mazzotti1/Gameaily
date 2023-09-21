package com.whatsthegame.models

data class DiaryGames(
    val first: GameInfo,
    val second: String
)

data class GameInfo(
    val id: Int,
    val gameName: String,
    val gameImage: String,
    val difficulty: String,
    val tips: String
)
