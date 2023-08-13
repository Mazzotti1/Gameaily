package com.whatsthegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.fragments.minigamesFragment
import com.whatsthegame.fragments.rankFragment


class MainActivity : AppCompatActivity() {

    private val layoutId = R.layout.activity_whats_the_game
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val startButton = findViewById<Button>(R.id.homeButtonStart)

        startButton.setOnClickListener {
            val intent = Intent(this, WhatsTheGameActivity::class.java)
            startActivity(intent)
        }

    }
}