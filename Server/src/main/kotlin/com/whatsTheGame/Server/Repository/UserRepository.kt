package com.whatsTheGame.Server.Repository

import com.whatsTheGame.Server.Entity.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<Users, Long> {
    fun findByEmail(email: String?): Users?
    fun findByName(name:String?): Users?

}