package com.whatsTheGame.Server.Services.Impl

import com.whatsTheGame.Server.Entity.Forms.UserForm
import com.whatsTheGame.Server.Entity.Users
import com.whatsTheGame.Server.IncorrectException.RegistroIncorretoException
import com.whatsTheGame.Server.Repository.UserRepository
import com.whatsTheGame.Server.Security.JwtToken
import com.whatsTheGame.Server.Services.IUsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

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
    override fun getRollsByUserId(userId: Long): Int? {
        val user = userRepository.findById(userId).orElse(null)
        return user?.rolls
    }

    override fun setRollsByUserId(userId: Long) {
        val userOptional = userRepository.findById(userId)
            val user = userOptional.get()
            user.rolls -= 1
            userRepository.save(user)
    }

    override fun setVipStatus(userId: Long) {
        val userOptional = userRepository.findById(userId)
        val user = userOptional.get()
        user.vip = true
        userRepository.save(user)
    }

    override fun getVipStatus(userId: Long): Boolean? {
        val user = userRepository.findById(userId).orElse(null)
        return user?.vip
    }

    override fun reduceRemaningLives(userId: Long) {
        val userOptional = userRepository.findById(userId)
        val user = userOptional.get()
        user.remainingLives -= 1
        userRepository.save(user)
    }

    override fun getRemainingLivesStatus(userId: Long): Int? {
        val user = userRepository.findById(userId).orElse(null)
        return user.remainingLives
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
            users.role,
            users.userAnswer,
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

    override fun sendPoints(userId: Long, pointsToAdd: Int): Boolean {
        val userOptional = userRepository.findById(userId)
        if (userOptional.isPresent) {
            val user = userOptional.get()
            val currentPoints = user.points ?: 0

            val updatedPoints = currentPoints + pointsToAdd

            val newRank = calculateRank(updatedPoints)
            val newDivision = calculateDivision(updatedPoints)

            user.points = updatedPoints
            user.rank = newRank
            user.division = newDivision

            userRepository.save(user)
            return true
        }
        return false
    }

    fun calculateRank(points: Int): String {
        return when{
            points in 0..499 -> "Bronze"
            points in 500..949 -> "Prata"
            points in 950..1399 -> "Ouro"
            points in 1400..1849 -> "Platina"
            points in 1850..2150 -> "Diamante"
            points >= 2150 -> "Diamante"
            else -> "Sem Rank"
        }
    }

    fun calculateDivision(points: Int): String {
        return when {
            points in 0..199 -> "Divisão III"
            points in 200..349 -> "Divisão II"
            points in 350..499 -> "Divisão I"
            points in 500..649 -> "Divisão III"
            points in 650..799 -> "Divisão II"
            points in 800..949 -> "Divisão I"
            points in 950..1099 -> "Divisão III"
            points in 1100..1249 -> "Divisão II"
            points in 1250..1399 -> "Divisão I"
            points in 1400..1549 -> "Divisão III"
            points in 1550..1699 -> "Divisão II"
            points in 1700..1849 -> "Divisão I"
            points in 1850..1999 -> "Divisão III"
            points in 2000..2149 -> "Divisão II"
            points >= 2150 -> "Division I"
            else -> "Sem Divisão"
        }
    }

    override fun updateUserAnswer(userId: Long) {
        val existingUser = userRepository.findById(userId)

        if (existingUser!!.isPresent) {
            val updatedUser = existingUser.get()
            updatedUser.userAnswer = true
            userRepository.save(updatedUser)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario não encontrado!")
        }
    }

    fun updateAllUsers() {
        val allUsers = userRepository.findAll()
        for (user in allUsers) {
            user.userAnswer = false
            user.rolls += 1
            user.remainingLives = 5
        }
        userRepository.saveAll(allUsers)
    }


}






