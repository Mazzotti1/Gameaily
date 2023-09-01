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

    var rank: String? = null

    var division: String? = null

    var points: Int? = null

    var token: String? = null

    var lifesWtg: Int = 5

    var lifesAnagrams: Int = 3

    var lifePistas: Int = 3

    var role : String = "USER"

    fun token(token: String?) {
        this.token = token.toString()
    }
}
