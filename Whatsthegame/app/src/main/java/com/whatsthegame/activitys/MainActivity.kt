package com.whatsthegame.activitys

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContentProviderCompat.requireContext

import com.whatsthegame.R
import com.whatsthegame.databinding.ActivityMainBinding


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

        val rankButton = findViewById<ImageButton>(R.id.homeButtonRank)

        rankButton.setOnClickListener {
            val intent = Intent(this, RankActivity::class.java)
            startActivity(intent)
        }

        val minigameButton = findViewById<ImageButton>(R.id.homeButtonMiniGames)

        minigameButton.setOnClickListener {
            val intent = Intent(this, MinigamesActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<ImageButton>(R.id.homeButtonSettings)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}