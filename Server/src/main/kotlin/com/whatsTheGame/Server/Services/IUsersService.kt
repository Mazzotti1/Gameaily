package com.whatsTheGame.Server.Services

import com.musclemate.server.entity.form.UserForm
import com.whatsTheGame.Server.Entity.Users

interface IUsersService {
    fun getAll(name: String?): List<Users?>?

    fun create(form: UserForm?): Users?
}