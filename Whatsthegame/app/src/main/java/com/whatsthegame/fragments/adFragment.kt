package com.whatsthegame.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.android.billingclient.api.*
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.whatsthegame.Api.ViewModel.SetUserVipViewModel
import com.whatsthegame.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [adFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class adFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var setUserVipViewModel: SetUserVipViewModel
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val authToken = sharedPreferences.getString("tokenJwt", "")
                println("Compra bem sucedida")
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
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClient = BillingClient.newBuilder(requireContext())
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

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ad, container, false)

        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val adButton = view.findViewById<Button>(R.id.adButton)
        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("tokenJwt", "")

        adButton.setOnClickListener {
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

                    val billingResult = billingClient!!.launchBillingFlow(requireActivity(), billingFlowParams)
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
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()
            }
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment adFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            adFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}