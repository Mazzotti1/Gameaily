package com.whatsthegame.activitys

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whatsthegame.R
import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.tutorial.TutorialWhatsThegame

class WhatsTheGameActivity : AppCompatActivity() {

    private val layoutId = R.layout.activity_whats_the_game
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment)


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

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            val initialStep = 1
            val title = "Bem-vindo à nossa tela!"
            val imageResId = R.drawable.diamante
            val tutorialDialog = TutorialWhatsThegame.newInstance(title, imageResId)
            tutorialDialog.show(supportFragmentManager, "tutorial_dialog")
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }

        setContentView(R.layout.activity_whats_the_game)
        setUpBottomNavigation()
    }
    private fun setUpBottomNavigation(){
        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragment)
        navView.setupWithNavController(navController)
    }
}
