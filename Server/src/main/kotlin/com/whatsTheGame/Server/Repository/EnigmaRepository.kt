package com.whatsTheGame.Server.Repository

import com.whatsTheGame.Server.Entity.Enigma
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnigmaRepository : JpaRepository<Enigma, Long> {

    fun findByEnigmaName(enigmaName: String?): Enigma

    fun findByTips(tips: String?): List<Enigma>

    fun findByDifficulty(difficulty: String?): List<Enigma>
}