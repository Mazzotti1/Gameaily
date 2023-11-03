package com.whatsthegame.whatsTheGameFragments


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.whatsthegame.R
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.whatsthegame.Api.ViewModel.DiaryGameViewModel
import com.whatsthegame.Api.ViewModel.LoginViewModel
import com.whatsthegame.Api.ViewModel.RegisterViewModel
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.launch


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
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        auth = FirebaseAuth.getInstance()
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

        logUserEnteredScreenEvent()

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
            signInGoogle()
        }

        return view
    }

    private fun logUserEnteredScreenEvent() {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "RightAnswerFragment")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }


    private lateinit var diaryGameViewModel: DiaryGameViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryGameViewModel = ViewModelProvider(this).get(DiaryGameViewModel::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("playerHasAnswer", true)
        editor.apply()

        diaryGameViewModel.game.observe(viewLifecycleOwner, Observer { game ->
            if (game != null) {
                val timer = game.second
                println("Timer : $timer")
                // Parse a string do timer para obter horas, minutos e segundos
                val timeParts = timer.split(":")
                val hours = timeParts[0].toLong()
                val minutes = timeParts[1].toLong()
                val seconds = timeParts[2].toLong()

                // Calcule o tempo total em milissegundos
                val totalTimeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000

                // Configure o CountDownTimer com o tempo total
                countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val remainingSeconds = seconds % 60

                        timerTextView = view.findViewById(R.id.timerTextView)

                        timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
                    }

                    override fun onFinish() {

                        timerTextView.text = "00:00:00"
                        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("playerHasAnswer", false)
                        editor.apply()
                    }
                }

                // Inicie o CountDownTimer
                countDownTimer.start()
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            diaryGameViewModel.fetchDiaryGame()
        }
    }

    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private val clientId = dotenv["CLIENT_ID"]!!
    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign-In failed
                println("Google sign-in failed: $e")
            }
        }
    }

    private var googleName: String? = null
    private var googleEmail: String? = null
    private var googleUid: String? = null


    private fun authAndRegister() {
        registerViewModel.registerUser(googleName!!, googleEmail!!, googleUid!!)
        registerViewModel.registerStatus.observe(viewLifecycleOwner) { registerStatus ->
            val inflater = layoutInflater
            val layout = inflater.inflate(R.layout.submit_layout, null)
            val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
            toastText.text = registerStatus
            val toast = Toast(requireContext())
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()

            if (registerStatus == "Conta criada com sucesso!") {
               login()
            } else {
                loginViewModel.loginUser(googleEmail!!, googleUid!!)
                loginViewModel.loginStatus.observe(viewLifecycleOwner) { loginStatus ->
                    if (loginStatus == "Logado com sucesso!") {
                        val inflater = layoutInflater
                        val layout = inflater.inflate(R.layout.submit_layout, null)
                        val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                        toastText.text = loginStatus
                        val toast = Toast(requireContext())
                        toast.duration = Toast.LENGTH_SHORT
                        toast.view = layout
                        toast.show()

                        val sharedPreferences = requireContext().getSharedPreferences(
                            "Preferences",
                            Context.MODE_PRIVATE
                        )
                        val token = loginViewModel.getToken()
                        val editor = sharedPreferences.edit()
                        editor.putString("tokenJwt", token)
                        editor.apply()
                        findNavController().navigate(R.id.action_rightAnswerFragment_to_posLoginFragment)
                    }
                }
            }
        }
    }
    private fun login() {
        loginViewModel.loginUser(googleEmail!!, googleUid!!)
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { loginStatus ->
            if (loginStatus == "Logado com sucesso!") {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = loginStatus
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()

                val sharedPreferences = requireContext().getSharedPreferences(
                    "Preferences",
                    Context.MODE_PRIVATE
                )
                val token = loginViewModel.getToken()
                println("Token do usuario: $token")
                val editor = sharedPreferences.edit()
                editor.putString("tokenJwt", token)
                editor.apply()
                findNavController().navigate(R.id.action_rightAnswerFragment_to_posLoginFragment)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        googleName = user.displayName
                        googleEmail = user.email
                        googleUid = user.uid
                        authAndRegister()
                    }
                } else {
                    println("Google sign-in failed")
                }
            }
        }
    override fun onResume() {
        super.onResume()
        if (::countDownTimer.isInitialized) {
            countDownTimer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }


    companion object {
        private const val RC_SIGN_IN = 9001
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