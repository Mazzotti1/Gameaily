package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Forms.UserForm
import com.whatsTheGame.Server.Entity.Users
import java.util.*
import javax.naming.AuthenticationException

interface IUsersService {
    fun getAll(name: String?): List<Users?>?

    fun create(form: UserForm?): Users?

    @Throws(AuthenticationException::class)
    fun login(email: String?, password: String?): Users?

    fun deleteAccount(userId: Long): Boolean

    fun sendPoints(userId: Long, points: Int): Boolean

    fun updateUserAnswer(userId: Long)

    fun getRollsByUserId(userId: Long): Int?

    fun setVipStatus(userId: Long)

    fun getVipStatus(userId: Long): Boolean?

    fun setRollsByUserId(userId: Long)

    fun reduceRemaningLives(userId: Long)

    fun getRemainingLivesStatus(userId: Long): Int?
}