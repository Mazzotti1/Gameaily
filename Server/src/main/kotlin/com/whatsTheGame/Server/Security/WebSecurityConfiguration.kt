package com.whatsTheGame.Server.Security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {


    @Bean
    fun encoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            //addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter())
            authorizeRequests {
                authorize("/users", permitAll)
                authorize("/users/register", permitAll)
                authorize(anyRequest, authenticated)
            }
            csrf { disable() }
            authorizeRequests {  }
            sessionManagement { SessionCreationPolicy.STATELESS }
            cors {  }
            headers {  }
            formLogin {disable()}
            httpBasic {}
        }
        return http.build()
    }
}