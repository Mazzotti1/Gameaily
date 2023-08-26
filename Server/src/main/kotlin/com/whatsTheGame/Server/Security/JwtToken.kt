package com.whatsTheGame.Server.Security

import io.github.cdimascio.dotenv.dotenv
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtToken{

    val dotenv = dotenv()
    val secretKey = dotenv["JWT_SECRET"]!!
    val tokenExpiry : String? = dotenv["TOKEN_EXPIRY"]

    private val userDetailsService: UserDetailsService? = null
    fun generateToken(
        id: Long?,
        name: String?,
        email: String?,
        rank: String?,
        division: String?,
        points: Int?,

    ): String? {
        val now = Date()
        val expiryDate = Date(now.time + tokenExpiry!!.toInt())

        val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

        return Jwts.builder()
            .setSubject(id.toString())
            .claim("id", id)
            .claim("name", name)
            .claim("email", email)
            .claim("rank", rank)
            .claim("division", division)
            .claim("points", points)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()

    }

}

