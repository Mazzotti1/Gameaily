package com.whatsthegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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

        val iconList = listOf(R.drawable.heartthin, R.drawable.heartthin, R.drawable.heartthin, R.drawable.heartthin, R.drawable.heartthin)
        val iconContainer = findViewById<LinearLayout>(R.id.hearts)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        for (iconResId in iconList) {
            val imageView = ImageView(this)
            imageView.setImageResource(iconResId)
            imageView.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.icon_size),
                resources.getDimensionPixelSize(R.dimen.icon_size)
            )
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.destaques))

            iconContainer.addView(imageView)
        }

    }
    private fun setUpBottomNavigation(){
        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragment)
        navView.setupWithNavController(navController)
    }

}
