package com.whatsTheGame.Server.Schedule

import com.whatsTheGame.Server.Services.Impl.UserServiceImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTask(private val userService: UserServiceImpl) {

    @Scheduled(cron = "0 0 0 * * ?") // Agende para executar à meia-noite todos os dias
    fun executeTask() {
        // Coloque o código que deseja executar aqui
        userService.updateAllUsers()
        println("Tarefa agendada executada às 00:00:00")
    }
}