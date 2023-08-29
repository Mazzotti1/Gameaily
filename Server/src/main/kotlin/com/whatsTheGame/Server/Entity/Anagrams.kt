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
@Table(name = "anagrams")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
class Anagrams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var wordName: String? = null

    var answer: String? = null

    var difficulty: String? = null

    var tips: String? = null

}
