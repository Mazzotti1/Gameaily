package com.whatsthegame.activitys

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whatsthegame.R

import com.whatsthegame.databinding.ActivityMainBinding



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
        setUpBottomNavigation()
    }
    private fun setUpBottomNavigation(){
        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragmentMinigames)
        navView.setupWithNavController(navController)
    }
}