package com.whatsthegame.activitys

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.whatsthegame.R

import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.tutorial.TutorialStep
import com.whatsthegame.tutorial.TutorialWhatsThegame

class RankActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragmentRank)

        when (item.itemId) {
            android.R.id.home -> {
                if (!navController.popBackStack()) {
                    finish()
                }
                return true
            }
            R.id.icon_bet -> {
                navController.navigate(R.id.rouletteFragment)
                return true
            }
            R.id.icon_gear -> {
                navController.navigate(R.id.settingsFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            val steps = listOf(
                TutorialStep("Bem-vindo ao Rank!", R.drawable.step1rank),
                TutorialStep("Aqui você pode ver em tempo real as posições do usuário, seus pontos e seus ranks!", R.drawable.step2rank),
                TutorialStep("Cada rank e divisão são baseados na sua pontuação, quanto mais pontos você ganha mais alto será o seu rank!", R.drawable.step3rank),
                TutorialStep("Os 3 melhores usuários recebem medalhas especiais", R.drawable.step4rank),
                TutorialStep("E você consegue ver a sua posição onde está a borda vermelha, e também pode usar a barra de pesquisa para pesquisar por usuários!", R.drawable.step5rank)
            )

            val tutorialDialog = TutorialWhatsThegame.newInstance(steps)
            tutorialDialog.show(supportFragmentManager, "tutorial_dialog")

            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }


    }
}