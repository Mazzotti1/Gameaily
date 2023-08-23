package com.whatsTheGame.Server.Services

import com.whatsTheGame.Server.Entity.Users

interface IUsersService {
    fun getAll(name: String?): List<Users?>?

}