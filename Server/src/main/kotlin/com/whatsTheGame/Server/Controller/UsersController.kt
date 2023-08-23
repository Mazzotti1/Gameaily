package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.DTOS.UsersDTO
import com.whatsTheGame.Server.Services.Impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersController {

    @Autowired
    private val service: UserServiceImpl? = null

    @GetMapping
    fun getAll(@RequestParam(value = "name", required = false) name: String?): List<UsersDTO?>? {
        val users = service!!.getAll(name)
        val usersDTOList = users?.map { user ->
            UsersDTO(id = user?.id, name = user?.name, email = user?.email,
            )
        }
        return usersDTOList
    }
}