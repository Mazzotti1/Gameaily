package com.whatsthegame.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}