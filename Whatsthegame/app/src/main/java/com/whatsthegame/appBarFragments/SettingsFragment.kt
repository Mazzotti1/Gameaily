package com.whatsthegame.appBarFragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.whatsthegame.Api.ViewModel.DeleteUserViewModel

import com.whatsthegame.R
import io.github.cdimascio.dotenv.dotenv

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteUserViewModel = ViewModelProvider(this).get(DeleteUserViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)


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