package com.whatsTheGame.Server.Controller

import com.musclemate.server.entity.form.UserForm
import com.whatsTheGame.Server.DTOS.UsersDTO
import com.whatsTheGame.Server.Entity.Users
import com.whatsTheGame.Server.Services.Impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UsersController {

    @Autowired
    private val service: UserServiceImpl? = null

    @GetMapping
    fun getAll(@RequestParam(value = "name", required = false) name: String?): List<UsersDTO?>? {
        val users = service!!.getAll(name)
        val usersDTOList = users?.map { user ->
            UsersDTO(
                id = user?.id, name = user?.name, email = user?.email,
            )
        }
        return usersDTOList
    }
    @PostMapping("/register")
    fun create(@RequestBody form: UserForm?): Users? {
        return service!!.create(form!!)
    }

}