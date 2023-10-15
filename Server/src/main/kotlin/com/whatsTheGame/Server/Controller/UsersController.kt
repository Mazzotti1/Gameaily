package com.whatsTheGame.Server.Controller

import com.whatsTheGame.Server.DTOS.UsersDTO
import com.whatsTheGame.Server.Entity.Forms.UserForm
import com.whatsTheGame.Server.Entity.Users
import com.whatsTheGame.Server.IncorrectException.RegistroIncorretoException
import com.whatsTheGame.Server.Services.Impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
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
                name = user?.name,
                rank = user?.rank,
                division = user?.division,
                points = user?.points
            )
        }
        return usersDTOList
    }

    @GetMapping("/rolls/{userId}")
    fun getRollsByUserId(@PathVariable userId: Long): Int? {
        return service!!.getRollsByUserId(userId)
    }

    @GetMapping("/vip/{userId}")
    fun getVipStatus(@PathVariable userId: Long): Boolean? {
        return service!!.getVipStatus(userId)
    }

    @PatchMapping("/change/vip/{userId}")
    fun setVipStatus(@PathVariable userId: Long) {
        return service!!.setVipStatus(userId)
    }

    @PostMapping("/register")
    fun create(@RequestBody form: UserForm?): ResponseEntity<out Any> {
        try {
            val user = service!!.create(form!!)
            return ResponseEntity(user, HttpStatus.CREATED)
        } catch (ex: RegistroIncorretoException) {
            val errorMessage = ex.message ?: "Ocorreu um erro durante a criação do usuário."
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
    }


    @PostMapping("/login")
    @Throws(AuthenticationException::class)
    fun login(@RequestBody form: UserForm): ResponseEntity<out Any> {
        try {
            val users: Users? = service?.login(form.email, form.password)
            return ResponseEntity.ok().body(users?.token)
        } catch (ex: RegistroIncorretoException) {
            val errorMessage = ex.message ?: "Ocorreu um erro durante a criação do usuário."
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/delete/{userId}")
    fun deleteAccount(@PathVariable userId: Long): ResponseEntity<String> {
        val deleted = service!!.deleteAccount(userId)
        return if (deleted) {
            ResponseEntity.ok("Conta excluída com sucesso.")
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/sendPoints/{userId}/{points}")
    fun sendPoints(@PathVariable userId: Long, @PathVariable points: Int): ResponseEntity<String> {
        val success = service!!.sendPoints(userId, points)
        return if (success) {
            ResponseEntity.ok("Pontuação atualizada com sucesso.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.")
        }
    }

}