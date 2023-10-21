package com.whatsthegame.appBarFragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.whatsthegame.Api.ViewModel.DeleteUserViewModel
import com.whatsthegame.R
import io.github.cdimascio.dotenv.dotenv
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var deleteUserViewModel: DeleteUserViewModel

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        // Você pode implementar a lógica relacionada a compras aqui
        // Por exemplo, verificar o resultado e processar as compras
    }

    private var billingClient: BillingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteUserViewModel = ViewModelProvider(this).get(DeleteUserViewModel::class.java)

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

    private var productDetails: ProductDetails? = null
    private lateinit var selectedOfferToken : String
    lateinit var mAdView : AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val adButton = view.findViewById<Button>(R.id.adButton)


        adButton.setOnClickListener {
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
                println("Produto esta nulo")
                // Handle the case when productDetails is null
                // You can display an error message or take appropriate action here
            }
        }

        val faqButton = view.findViewById<Button>(R.id.faq)
        val aboutButton = view.findViewById<Button>(R.id.about)
        val logoutButton = view.findViewById<Button>(R.id.logout)
        val deleteUserButton = view.findViewById<Button>(R.id.deleteAccount)
        faqButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_faqFragment)
        }

        aboutButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
        }

        logoutButton.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val authToken = sharedPreferences.getString("tokenJwt", "")

            if (!authToken.isNullOrEmpty()) {
                try {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
                    alertDialogBuilder.setTitle("Confirmação")
                    alertDialogBuilder.setMessage("Tem certeza que deseja sair da sua conta?")

                    alertDialogBuilder.setPositiveButton("Sim") { _, _ ->
                        val editor = sharedPreferences.edit()
                        editor.remove("tokenJwt")
                        editor.apply()
                        findNavController().navigate(R.id.action_settingsFragment_to_whatsTheGame)
                        FirebaseAuth.getInstance().signOut();
                        signOutGoogle()
                    }

                    alertDialogBuilder.setNegativeButton("Cancelar") { _, _ ->
                    }
                    alertDialogBuilder.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Não há nenhuma conta logada nesse momento."
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()
            }
        }

        deleteUserButton.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val authToken = sharedPreferences.getString("tokenJwt", "")

            if (!authToken.isNullOrEmpty()) {
                try {
                    val decodedJWT: DecodedJWT = JWT.decode(authToken)

                    val userId = decodedJWT.subject

                    val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
                    alertDialogBuilder.setTitle("Confirmação")
                    alertDialogBuilder.setMessage("Tem certeza que deseja excluir sua conta? Essa ação é irreversível.")

                    alertDialogBuilder.setPositiveButton("Sim") { _, _ ->
                        // O usuário confirmou, então você pode prosseguir com a exclusão da conta.
                        deleteUserViewModel.deleteUser(userId.toLong())
                        val editor = sharedPreferences.edit()
                        editor.remove("tokenJwt")
                        editor.apply()
                        findNavController().navigate(R.id.action_settingsFragment_to_whatsTheGame)
                    }

                    alertDialogBuilder.setNegativeButton("Cancelar") { _, _ ->

                    }

                    alertDialogBuilder.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Não há nenhuma conta logada nesse momento."
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()
            }


        }


        return view
    }



    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private val clientId = dotenv["CLIENT_ID"]!!
    private fun signOutGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}