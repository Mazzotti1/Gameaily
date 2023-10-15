package com.whatsthegame.minigamesFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.whatsthegame.Api.ViewModel.*
import com.whatsthegame.R
import com.whatsthegame.models.GuessAnagram
import com.whatsthegame.models.GuessDiaryGame
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnagramaSolverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnagramaSolverFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var guessAnagramViewModel: GuessAnagramViewModel
    private lateinit var userVipViewModel: UserVipViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guessAnagramViewModel = ViewModelProvider(this).get(GuessAnagramViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var tips: String? = null
    private var answer: GuessAnagram? = null
    private lateinit var anagramViewModel: AnagramsViewModel
    private var mInterstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var watchedRewardAd = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_anagrama_solver, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val adController = sharedPreferences.getBoolean("adControl", false)

        val timestamp = sharedPreferences.getLong("adControlTimestamp", 0L)
        val vinteEQuatroHoras = 24 * 60 * 60 * 1000
        if (System.currentTimeMillis() - timestamp >= vinteEQuatroHoras) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("adControl", false)
            editor.apply()
        }

        val authToken = sharedPreferences.getString("tokenJwt", "")
        if(!authToken.isNullOrEmpty()){
            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val userId = decodedJWT.subject

            userVipViewModel = ViewModelProvider(this).get(UserVipViewModel::class.java)
            userVipViewModel.vip.observe(viewLifecycleOwner) { vip ->
                val userVip = vip ?: false
                if (!userVip && !adController) {
                    loadAd()
                }
            }

            userVipViewModel.getVip(userId.toLong())
        }


       loadAdRewarded()

        val pointsCounter = view.findViewById<TextView>(R.id.points)
        var points = 0
        val lifesCounter = view.findViewById<TextView>(R.id.textViewLifes)
        var remainingLives = 3
        var submitButtonClickCount = 0

        val iconList = listOf(
            R.drawable.heartthin,
            R.drawable.heartthin,
            R.drawable.heartthin,
        )
        val iconContainer = view.findViewById<LinearLayout>(R.id.hearts)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        for (iconResId in iconList) {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(iconResId)
            imageView.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.icon_size),
                resources.getDimensionPixelSize(R.dimen.icon_size)
            )
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.destaques))

            iconContainer.addView(imageView)
        }


        anagramViewModel = ViewModelProvider(this).get(AnagramsViewModel::class.java)
        val textViewDifficulty= view.findViewById<TextView>(R.id.difficulty)
        val textViewAnagram = view.findViewById<TextView>(R.id.anagram)
        anagramViewModel.anagram.observe(viewLifecycleOwner, Observer { anagram ->
            if (anagram != null){
                val wordname = anagram.wordName
                answer = GuessAnagram(anagram.answer)
                val gameDifficulty = anagram.difficulty
                tips = anagram.tips
                val colorResId = when (gameDifficulty) {
                    "Fácil" -> R.color.win
                    "Médio" -> R.color.secundaria
                    "Difícil" -> R.color.destaques
                    else -> android.R.color.white
                }
                activity?.runOnUiThread {
                    textViewAnagram.text = "$wordname"
                    textViewDifficulty.text = "$gameDifficulty"

                    val color = ContextCompat.getColor(requireContext(), colorResId)
                    textViewDifficulty.setTextColor(color)
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            anagramViewModel.fetchAnagramGame()
        }

        val tipButton = view.findViewById<ImageButton>(R.id.tipButton)
        val tipTextView = view.findViewById<TextView>(R.id.tipText)

        tipButton.setOnClickListener {
            tipTextView.text = tips
        }

        val editText = view.findViewById<EditText>(R.id.editTextAnswer)
        val submitButton = view.findViewById<Button>(R.id.submitButton)


        submitButton.setOnClickListener {
            val text = editText.text
            val choosedAnswer = GuessAnagram(text.toString())

            if (remainingLives > 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (!text.isNullOrEmpty()) {
                        if (choosedAnswer != answer) {

                            remainingLives--
                            lifesCounter.text = "$remainingLives vidas restantes"

                            if (remainingLives <= 0) {

                                if (watchedRewardAd) {
                                    loadAd()
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd?.show(requireActivity())
                                        submitButtonClickCount = 0
                                    } else {
                                        println("O anúncio intersticial ainda não estava pronto.")
                                    }
                                    findNavController().navigate(R.id.action_anagramaSolverFragment2_to_gameOverMinigamesFragment2)
                                } else {

                                    val alertDialogBuilder = AlertDialog.Builder(
                                        ContextThemeWrapper(
                                            requireContext(),
                                            R.style.AlertDialogStyle
                                        )
                                    )

                                    alertDialogBuilder.setTitle("Sem vidas restantes")
                                    alertDialogBuilder.setMessage("Você gostaria de assistir um anúncio para mais uma última chance?")

                                    alertDialogBuilder.setPositiveButton("Sim") { dialog, which ->
                                        loadAdRewarded()
                                        rewardedAd?.let { ad ->
                                            ad.show(
                                                requireActivity(),
                                                OnUserEarnedRewardListener { rewardItem ->
                                                    watchedRewardAd = true
                                                    remainingLives = 1
                                                    lifesCounter.text =
                                                        "$remainingLives vidas restantes"
                                                })
                                        } ?: run {
                                            println("The rewarded ad wasn't ready yet.")
                                        }
                                    }


                                    alertDialogBuilder.setNegativeButton("Não") { dialog, which ->
                                        if (mInterstitialAd != null) {
                                            mInterstitialAd?.show(requireActivity())
                                        } else {
                                            println("O anúncio intersticial ainda não estava pronto.")
                                        }
                                        findNavController().navigate(R.id.action_anagramaSolverFragment2_to_gameOverMinigamesFragment2)
                                    }
                                    alertDialogBuilder.create().show()
                                }

                            } else {

                                val inflater = layoutInflater
                                val layout = inflater.inflate(R.layout.submit_layout, null)
                                val toastText =
                                    layout.findViewById<TextView>(R.id.empty_submit_text)
                                toastText.text = "Resposta errada!"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layout
                                toast.show()


                                val editText = view.findViewById<EditText>(R.id.editTextAnswer)
                                editText.text.clear()
                            }
                        } else {
                            guessAnagramViewModel.guessAnagram(choosedAnswer)

                            points++
                            pointsCounter.text = "$points Pontos"
                            editText.text.clear()
                            tipTextView.text = ""
                            submitButtonClickCount++
                            println("Clicks do botão: $submitButtonClickCount")
                            if (submitButtonClickCount >= 3) {
                                loadAd()
                                if (mInterstitialAd != null) {
                                    mInterstitialAd?.show(requireActivity())
                                    submitButtonClickCount = 0
                                } else {
                                    println("O anúncio intersticial ainda não estava pronto.")
                                }
                            }

                            anagramViewModel.anagram.observe(viewLifecycleOwner, Observer { anagram ->
                                if (anagram != null){
                                    val wordname = anagram.wordName
                                    answer = GuessAnagram(anagram.answer)
                                    val gameDifficulty = anagram.difficulty
                                    tips = anagram.tips
                                    val colorResId = when (gameDifficulty) {
                                        "Fácil" -> R.color.win
                                        "Médio" -> R.color.secundaria
                                        "Difícil" -> R.color.destaques
                                        else -> android.R.color.white
                                    }
                                    activity?.runOnUiThread {
                                        textViewAnagram.text = "$wordname"
                                        textViewDifficulty.text = "$gameDifficulty"

                                        val color = ContextCompat.getColor(requireContext(), colorResId)
                                        textViewDifficulty.setTextColor(color)
                                    }
                                }
                            })
                            viewLifecycleOwner.lifecycleScope.launch {
                                anagramViewModel.fetchAnagramGame()
                            }
                        }

                    } else {
                        val inflater = layoutInflater
                        val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                        val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                        toastText.text = "Selecione uma resposta antes de enviar!"
                        val toast = Toast(requireContext())
                        toast.duration = Toast.LENGTH_LONG
                        toast.view = layout
                        toast.show()
                    }
                }
            } else {

                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Você não tem vidas restantes!"
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()
            }
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                editText.clearFocus()
            }
            false
        }

        return view
    }

    private fun loadAd(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                println("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun loadAdRewarded(){
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(),"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError?.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                println("Ad was loaded.")
                rewardedAd = ad
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnagramaSolverFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnagramaSolverFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}