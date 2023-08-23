package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Users
import com.whatsTheGame.Server.Repository.UserRepository
import com.whatsTheGame.Server.Services.IUsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl @Autowired constructor(private val userRepository: UserRepository) : IUsersService {

    override fun getAll(name: String?): List<Users?>? {
        return userRepository!!.findAll()
    }

}