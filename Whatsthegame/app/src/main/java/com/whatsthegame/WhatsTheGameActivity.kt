package com.whatsthegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whatsthegame.databinding.ActivityMainBinding

class WhatsTheGameActivity : AppCompatActivity() {

    private val layoutId = R.layout.activity_whats_the_game
    private lateinit var viewBinding: ActivityMainBinding

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
