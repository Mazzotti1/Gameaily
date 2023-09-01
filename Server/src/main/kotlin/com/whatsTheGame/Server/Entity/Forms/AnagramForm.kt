package com.whatsTheGame.Server.Entity.Forms

import jakarta.persistence.Column
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
class AnagramForm {
    @Column(unique = true)
    var wordName: String? = null

    var answer: String? = null

    var difficulty: String? = null

    var tips: String? = null
}