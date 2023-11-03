package com.whatsthegame.activitys



import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.whatsthegame.Api.ViewModel.UserVipViewModel

import com.whatsthegame.R
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    private lateinit var userVipViewModel: UserVipViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) {}
        setContentView(R.layout.activity_main)

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("tokenJwt", "")
        val userVipViewModel = ViewModelProvider(this).get(UserVipViewModel::class.java)

        if (authToken.isNullOrEmpty()) {
            mAdView = findViewById(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        } else {
            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val userId = decodedJWT.subject

            userVipViewModel.vip.observe(this) { vip ->
                val userVip = vip ?: false
                if (!userVip) {
                    mAdView = findViewById(R.id.adView)
                    val adRequest = AdRequest.Builder().build()
                    mAdView.loadAd(adRequest)
                }
            }

            userVipViewModel.getVip(userId.toLong())
        }


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