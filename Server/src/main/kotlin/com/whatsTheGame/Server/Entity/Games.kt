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
@Table(name = "games")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
class Games {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var gameName: String? = null

    @Column(unique = true)
    var gameImage: String? = null

    var difficulty: String? = null

    var tips: String? = null

}
