package com.whatsTheGame.Server.Repository

import com.whatsTheGame.Server.Entity.Anagrams
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnagramsRepository : JpaRepository<Anagrams, Long> {

    fun findByWordName(wordName: String?): Anagrams

    fun findByTips(tips: String?): List<Anagrams>

    fun findByDifficulty(difficulty: String?): List<Anagrams>
}