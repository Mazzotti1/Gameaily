package com.whatsTheGame.Server.Entity.Forms

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
class GamesForm {
    var gameName: String? = null

    var gameImage: String? = ""

    var difficulty: String? = null

    var tips: String? = null

}