package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Forms.UserForm
import com.whatsTheGame.Server.Entity.Users
import javax.naming.AuthenticationException

interface IUsersService {
    fun getAll(name: String?): List<Users?>?

    fun create(form: UserForm?): Users?

    @Throws(AuthenticationException::class)
    fun login(email: String?, password: String?): Users?

    fun deleteAccount(userId: Long): Boolean

    fun sendPoints(userId: Long, points: Int): Boolean
}