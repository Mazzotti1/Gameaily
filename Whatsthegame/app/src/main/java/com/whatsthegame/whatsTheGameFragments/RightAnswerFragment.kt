package com.whatsthegame.whatsTheGameFragments


import android.app.Activity.RESULT_OK

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


import com.whatsthegame.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RightAnswerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RightAnswerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("904878821368-q1n94navrfjb25pns9ba1mv23jr9ce0a.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)


        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_right_answer, container, false)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_rightAnswerFragment_to_registerFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_rightAnswerFragment_to_loginFragment)
        }

        val googleButton = view.findViewById<ImageButton>(R.id.googleButton)
        googleButton.setOnClickListener {
            signIn()
        }



        countDownTimer = object : CountDownTimer(86400000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val remainingSeconds = seconds % 60

                timerTextView = view.findViewById(R.id.timerTextView)

                timerTextView.text =
                    String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
            }

            override fun onFinish() {
                // O timer de 24 horas chegou ao fim
                timerTextView.text = "00:00:00"
            }
        }

        return view
    }

    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        openActivity.launch(intent)

    }

    var openActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val email = account.email
                    println("Conta google: $account")
                    println("Conta email: $email")
                } else {
                    println("Conta Google nula")
                }
            } catch (exception: ApiException) {
                println("Erro aqui: $exception")
            }
        }




    override fun onResume() {
        super.onResume()
        // Iniciar o timer quando a tela estiver visível
        countDownTimer.start()
    }

    override fun onPause() {
        super.onPause()
        // Parar o timer quando a tela não estiver visível para economizar recursos
        countDownTimer.cancel()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RightAnswerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RightAnswerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}