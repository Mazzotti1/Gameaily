package com.whatsTheGame.Server.Repository

import com.whatsTheGame.Server.Entity.Games
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GamesRepository : JpaRepository<Games, Long> {

    fun findByGameName(gameName: String?): List<Games>

    fun findByTips(tips: String?): List<Games>

    fun findByDifficulty(difficulty: String?): List<Games>
}
