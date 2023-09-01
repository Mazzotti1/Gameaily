package com.whatsTheGame.Server.Entity.Forms

import jakarta.persistence.Column
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserForm {
    @Column(unique = true)
    var name: String? = null

    @Column(unique = true)
    var email: String? = null

    var password: String? = null

    var rank: String? = null

    var division: String? = null

    var points: Int? = null

}
