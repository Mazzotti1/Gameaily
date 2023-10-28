package com.whatsTheGame.Server.Entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
class Users {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var name: String? = null

    @Column(unique = true)
    var email: String? = null

    var password: String? = null

    var rank: String = ""

    var division: String = ""

    var points: Int = 0

    var token: String? = null

    var role : String = "USER"

    var rolls: Int = 1

    var vip: Boolean = false

    var remainingLives: Int = 5

    var userAnswer : Boolean = false
    fun token(token: String?) {
        this.token = token.toString()
    }
}
