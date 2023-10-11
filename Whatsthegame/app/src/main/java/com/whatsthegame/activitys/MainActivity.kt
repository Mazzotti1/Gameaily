package com.whatsthegame.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.whatsthegame.Fcm.NotificationScheduler

import com.whatsthegame.R


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val notificationScheduler = NotificationScheduler()
        notificationScheduler.scheduleDailyNotification(this)


        val startButton = findViewById<Button>(R.id.homeButtonStart)
        startButton.setOnClickListener {
                val intent = Intent(this, WhatsTheGameActivity::class.java)
                startActivity(intent)

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "start_button")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Botão Start")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "minigame_button")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Minigame Button")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }

        val settingsButton = findViewById<ImageButton>(R.id.homeButtonSettings)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }


}