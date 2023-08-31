package com.whatsTheGame.Server.Services.Impl

import com.musclemate.server.entity.form.UserForm
import com.whatsTheGame.Server.Entity.Users
import com.whatsTheGame.Server.IncorrectException.RegistroIncorretoException
import com.whatsTheGame.Server.Repository.UserRepository
import com.whatsTheGame.Server.Security.JwtToken
import com.whatsTheGame.Server.Services.IUsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val jwtToken: JwtToken
) : IUsersService {

     fun getPasswordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }


    override fun getAll(name: String?): List<Users?>? {
        return userRepository!!.findAll()
    }

    @Throws(RegistroIncorretoException::class)
    override fun create(form: UserForm?): Users {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        val errorMessages = mutableListOf<String>()
        if (!form!!.email?.matches(emailRegex)!!) {
            throw RegistroIncorretoException("O email informado está incorreto.")
        }
        if (userRepository.existsByEmail(form.email!!)) {
            throw RegistroIncorretoException("Já existe um usuário com o mesmo email.")
        }
        if (userRepository.existsByName(form.name!!)) {
            throw RegistroIncorretoException("Já existe um usuário com o mesmo nome.")
        }
        val users = Users()
        users.name = form.name
        users.email = form.email
        val rawPassword: String? = form.password

        if (rawPassword != null && rawPassword.length >= 6) {
            users.password = getPasswordEncoder()?.encode(rawPassword)
            return userRepository.save(users)
        } else {
            throw RegistroIncorretoException("A senha precisa ter no mínimo 6 caracteres.")
        }
    }

    @Throws(RegistroIncorretoException::class)
    override fun login(email: String?, password: String?): Users? {
        val users: Users = userRepository.findByEmail(email) ?: throw RegistroIncorretoException("Usuario nao encontrado")
        if (!getPasswordEncoder()!!.matches(password, users.password)) {
            throw RegistroIncorretoException("Senha incorreta")
        }
        val token: String? = jwtToken.generateToken(
            users.id,
            users.name,
            users.email,
            users.rank,
            users.division,
            users.points,
        )
        users.token(token)
        return users
    }

    override fun deleteAccount(id: Long): Boolean {
        val id = userRepository.findById(id)
        if (id.isPresent) {
            userRepository.delete(id.get())
            return true
        }
        return false
    }

}






