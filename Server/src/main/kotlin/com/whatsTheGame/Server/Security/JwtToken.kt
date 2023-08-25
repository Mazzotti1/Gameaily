package com.whatsTheGame.Server.Security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtToken {

    fun generateToken(
        id: Long?,
        name: String?,
        email: String?,
        rank: String?,
        division: String?,
        points: Int?,

    ): String? {
        val now = Date()
        val expiryDate = Date(now.time + 3600000)

        val key = Keys.secretKeyFor(SignatureAlgorithm.HS512)

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