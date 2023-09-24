package com.whatsthegame.accountFragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.whatsthegame.Api.ViewModel.LoginViewModel
import com.whatsthegame.Api.ViewModel.SendPointsViewModel
import com.whatsthegame.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
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
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        val editTextSenha = view.findViewById<EditText>(R.id.editTextSenha)

        val submitLoginButton = view.findViewById<Button>(R.id.submitLoginButton)
        submitLoginButton.setOnClickListener {
            val email = capitalizeWords(editTextEmail.text.toString())
            val password = editTextSenha.text.toString()

            loginViewModel.loginUser(email, password)
        }
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { loginStatus ->
            val inflater = layoutInflater
            val layout = inflater.inflate(R.layout.submit_layout, null)
            val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
            toastText.text = loginStatus
            val toast = Toast(requireContext())
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()

            if (loginStatus == "Logado com sucesso!" ) {

                val sharedPreferences =
                    requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val playerAnswer = sharedPreferences.getBoolean("playerHasAnswer", false)

               /* if (playerAnswer) {
                    val inflater = layoutInflater
                    val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                    val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                    toastText.text =
                        "Você já acertou o jogo hoje, novos pontos não serão computados!"
                    val toast = Toast(requireContext())
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = layout
                    toast.show()

                    val token = loginViewModel.getToken()
                    val editor = sharedPreferences.edit()
                    editor.putString("tokenJwt", token)
                    editor.apply()
                    findNavController().navigate(R.id.action_loginFragment_to_rankNavbar)

                } else { */

                    val token = loginViewModel.getToken()
                    val editor = sharedPreferences.edit()
                    editor.putString("tokenJwt", token)
                    editor.apply()
                    findNavController().navigate(R.id.action_loginFragment_to_posLoginFragment)
                }
            }

        //} Verificação se o usuario ja acertou

        view.setOnClickListener {
            hideKeyboard()
        }
        return view
    }



    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        if (currentFocusView != null) {
            imm.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    fun capitalizeWords(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.capitalize() }
        return capitalizedWords.joinToString(" ")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}