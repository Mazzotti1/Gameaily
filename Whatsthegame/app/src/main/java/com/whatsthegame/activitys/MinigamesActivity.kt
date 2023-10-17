package com.whatsthegame.activitys

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whatsthegame.R

import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.tutorial.TutorialStep
import com.whatsthegame.tutorial.TutorialWhatsThegame


class MinigamesActivity : AppCompatActivity() {

    private val layoutId = R.layout.activity_minigames
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragmentMinigames)

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
        setContentView(R.layout.activity_minigames)

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            val steps = listOf(
                TutorialStep("Bem-vindo aos Minigames!", R.drawable.step1minigame),
                TutorialStep("Aqui você você pode escolher entre os minigames disponíveis", R.drawable.step2minigame),
                TutorialStep("No Anagrama Solver, você recebe um anagrama de uma palavra aleatória e seu objetivo é acumular o máximo de pontos!", R.drawable.step3minigame),
                TutorialStep("No Enigma você recebe uma charada e a dificuldade e você tem que tentar advinhar a reposta e acumular o máximo de pontuações.", R.drawable.step4minigame),
                TutorialStep("E se você se cansar dos anúncios, você pode me apoiar em troca de parar de ver eles!", R.drawable.step5wtg)
            )

            val tutorialDialog = TutorialWhatsThegame.newInstance(steps)
            tutorialDialog.show(supportFragmentManager, "tutorial_dialog")

            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }


    }

}