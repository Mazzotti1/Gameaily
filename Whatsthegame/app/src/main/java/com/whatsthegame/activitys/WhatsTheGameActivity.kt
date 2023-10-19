package com.whatsthegame.activitys

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.billingclient.api.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.whatsthegame.R
import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.tutorial.TutorialStep
import com.whatsthegame.tutorial.TutorialWhatsThegame


class WhatsTheGameActivity : AppCompatActivity() {


    private lateinit var billingClient: BillingClient
    private val sku = "123456"
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment)


        when (item.itemId) {
            android.R.id.home -> {
                if (!navController.popBackStack()) {
                    finish()
                }
                return true
            }
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

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
        val steps = listOf(
            TutorialStep("Bem-vindo ao Whats The game!", R.drawable.step1wtg),
            TutorialStep("Aqui você vai ter diarimente uma capa de um jogo aleatório, e você pode tentar advinhar para ganhar alguns pontos!", R.drawable.step2wtg),
            TutorialStep("Mas calma! Se você quer ser o melhor tem que se atentar a algumas coisas!", R.drawable.step3wtg),
            TutorialStep("As pontuações são calculadas baseados nas suas ações, se usou dicas ou não, quanto tempo demorou para descobrir, quantas vidas você perdeu no processo, e outras coisas...", R.drawable.step4wtg),
            TutorialStep("E claro, se você quiser me apoiar a continuar atualizando o What's The Game? você é sempre bem vindo para virar VIP, dessa forma você evita ver anúncios e me ajuda muito!", R.drawable.step5wtg)
        )

        val tutorialDialog = TutorialWhatsThegame.newInstance(steps)
        tutorialDialog.show(supportFragmentManager, "tutorial_dialog")

        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }


        setContentView(R.layout.activity_whats_the_game)
        setUpBillingClient()
        setUpBottomNavigation()
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    println("O BillingClient está pronto.")
                }
            }

            override fun onBillingServiceDisconnected() {
                println(" Tente reiniciar a conexão na próxima solicitação ao Google Play chamando o método startConnection().")

            }
        })
    }
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                // Processar a compra aqui, fornecer conteúdo ao usuário, etc.
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                println(" Tratar um erro causado pelo usuário cancelando o fluxo de compra.")

            } else {
                println("Tratar outros códigos de erro.")

            }
        }

    private fun setUpBottomNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragment)
        navView.setupWithNavController(navController)

        val adNavbarItem = navView.menu.findItem(R.id.adNavbar)

        adNavbarItem.setOnMenuItemClickListener {
            // Iniciar o processo de compra quando o item da barra de navegação é clicado
            val skuList = ArrayList<String>()
            skuList.add(sku)

            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    val skuDetails = skuDetailsList[0]
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()
                    val responseCode = billingClient.launchBillingFlow(this, billingFlowParams)
                }
            }

            true
        }
    }
}
