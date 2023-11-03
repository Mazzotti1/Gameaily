package com.whatsthegame.activitys

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.billingclient.api.*
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.whatsthegame.Api.ViewModel.DeleteUserViewModel
import com.whatsthegame.Api.ViewModel.SetUserVipViewModel
import com.whatsthegame.R
import com.whatsthegame.databinding.ActivityMainBinding
import com.whatsthegame.tutorial.TutorialStep
import com.whatsthegame.tutorial.TutorialWhatsThegame


class WhatsTheGameActivity : AppCompatActivity() {
    private lateinit var setUserVipViewModel: SetUserVipViewModel
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val authToken = sharedPreferences.getString("tokenJwt", "")
                println("Compra bem sucedida")
                val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogStyle)

                dialogBuilder.setMessage("Parabéns! Você agora é um membro VIP!")
                dialogBuilder.setPositiveButton("Fechar") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = dialogBuilder.create()
                dialog.show()
                val decodedJWT: DecodedJWT = JWT.decode(authToken)
                val userId = decodedJWT.subject
                setUserVipViewModel.setVipStatus(userId.toLong())

                setUserVipViewModel.setVipStatus.observe(this, Observer { response ->
                    println("Resposta da api: $response")
                })
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            println("Compra cancelada pelo usuário")
        } else {
            println("Erro durante a compra. Código de resposta: ${billingResult.responseCode}")
        }
    }

    private var billingClient: BillingClient? = null

    private var productDetails: ProductDetails? = null
    private lateinit var selectedOfferToken : String
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
        setContentView(R.layout.activity_whats_the_game)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        // Conecte-se ao BillingClient
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                // Verifique se a configuração do BillingClient foi concluída com sucesso
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                    val productDetailsParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("remove_ad")
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()

                    billingClient?.queryProductDetailsAsync(productDetailsParams) { billingResult, productDetailsList ->
                        // Verifique o resultado da consulta
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            println("Passou produtos: $productDetailsList")
                            productDetails = productDetailsList[0]
                            val product = productDetailsList[0]
                            val offers = product.subscriptionOfferDetails
                            if (!offers.isNullOrEmpty()) {
                                selectedOfferToken = offers[0].offerToken
                            } else {
                                // Não há ofertas disponíveis
                            }

                        } else {
                            println("não pegou nenhum produtos")

                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                billingClient?.startConnection(this)
            }
        })

        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
        val steps = listOf(
            TutorialStep("Bem-vindo ao Gameaily!", R.drawable.step1wtg),
            TutorialStep("Aqui você vai ter diarimente uma capa de um jogo aleatório, e você pode tentar advinhar para ganhar alguns pontos!", R.drawable.step2wtg),
            TutorialStep("Mas calma! Se você quer ser o melhor tem que se atentar a algumas coisas!", R.drawable.step3wtg),
            TutorialStep("As pontuações são calculadas baseados nas suas ações, se usou dicas ou não, quanto tempo demorou para descobrir, quantas vidas você perdeu no processo, e outras coisas...", R.drawable.step4wtg),
            TutorialStep("E claro, se você quiser me apoiar a continuar atualizando o What's The Game? você é sempre bem vindo para virar VIP, dessa forma você evita ver anúncios e me ajuda muito!", R.drawable.step5wtg)
        )

        val tutorialDialog = TutorialWhatsThegame.newInstance(steps)
        tutorialDialog.show(supportFragmentManager, "tutorial_dialog")

        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }
        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val adNavItem = navView.menu.findItem(R.id.adNavbar)
        val sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("tokenJwt", "")

        adNavItem.setOnMenuItemClickListener {
            if (!authToken.isNullOrEmpty()) {
                if (productDetails != null) {
                    val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails!!)
                        .setOfferToken(selectedOfferToken)
                        .build()

                    val productDetailsParamsList = listOf(productDetailsParams)

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                    val billingResult = billingClient!!.launchBillingFlow(this, billingFlowParams)
                } else {
                    println("Produto está nulo")
                    // Handle the case when productDetails is null
                    // You can display an error message or take appropriate action here
                }
            } else {
                println("authToken está vazio ou nulo")
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Primeiro esteja logado em uma conta para poder adquirir o bloqueador de anúncios!"
                val toast = Toast(this)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()
            }
            false
        }

        val navController = findNavController(R.id.fragment)
        navView.setupWithNavController(navController)

    }
}
