package com.whatsthegame.activitys

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
            R.id.icon_bet -> {
                navController.navigate(R.id.rouletteFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whats_the_game)
        setUpBottomNavigation()

    }
    private fun setUpBottomNavigation(){
        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragment)
        navView.setupWithNavController(navController)
    }


}
