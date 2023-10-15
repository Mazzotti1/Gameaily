package com.whatsTheGame.Server.Security

import JwtAuthenticationFilter
import io.github.cdimascio.dotenv.dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration{

    val dotenv = dotenv()
    val routeA = dotenv["ROUTE_A"]!!
    val routeB = dotenv["ROUTE_B"]!!
    val routeAB = dotenv["ROUTE_AB"]!!
    val routeC = dotenv["ROUTE_C"]!!
    val routeD = dotenv["ROUTE_D"]!!
    val routeE = dotenv["ROUTE_E"]!!
    val routeF = dotenv["ROUTE_F"]!!
    val routeG = dotenv["ROUTE_G"]!!
    val routeH = dotenv["ROUTE_H"]!!
    val routeJ = dotenv["ROUTE_J"]!!
    val routeK = dotenv["ROUTE_K"]!!
    val routeL = dotenv["ROUTE_L"]!!
    val routeM = dotenv["ROUTE_M"]!!
    val routeN = dotenv["ROUTE_N"]!!
    val routeO = dotenv["ROUTE_O"]!!
    val routeP = dotenv["ROUTE_P"]!!
    val routeQ = dotenv["ROUTE_Q"]!!
    val routeR = dotenv["ROUTE_R"]!!
    val routeAdminA = dotenv["ROUTE_ADMIN_A"]!!
    val routeAdminB = dotenv["ROUTE_ADMIN_B"]!!
    val routeAdminC = dotenv["ROUTE_ADMIN_C"]!!
    @Bean
    fun encoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize(routeA, permitAll)
                authorize(routeB, permitAll)
                authorize(routeAB, permitAll)
                authorize(routeC, permitAll)
                authorize(routeD, permitAll)
                authorize(routeE, permitAll)
                authorize(routeF, permitAll)
                authorize(routeG, permitAll)
                authorize(routeH, permitAll)
                authorize(routeJ, permitAll)
                authorize(routeK, permitAll)
                authorize(routeL, permitAll)
                authorize(routeR, permitAll)

                authorize(routeM, authenticated)
                authorize(routeN, authenticated)
                authorize(routeO, authenticated)
                authorize(routeP, authenticated)
                authorize(routeQ, authenticated)

                authorize(routeAdminA, authenticated)
                authorize(routeAdminB, authenticated)
                authorize(routeAdminC, authenticated)

                authorize(anyRequest, authenticated)
            }
            cors {  }
            headers { frameOptions { disable() } }
            csrf { disable() }
            sessionManagement {SessionCreationPolicy.STATELESS}
            authorizeRequests {  }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter(JwtToken()))
            formLogin {disable()}
            httpBasic {}
        }

        return http.build()
    }
}