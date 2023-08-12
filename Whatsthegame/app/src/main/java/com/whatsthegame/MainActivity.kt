package com.whatsthegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.homeButtonStart)

        startButton.setOnClickListener {
            val intente = Intent(this, WhatsTheGameActivity::class.java)
            startActivity(intent)
        }
    }
}