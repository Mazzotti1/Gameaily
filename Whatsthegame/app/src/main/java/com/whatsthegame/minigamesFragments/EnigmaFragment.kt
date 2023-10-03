package com.whatsthegame.minigamesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.whatsthegame.Api.ViewModel.EnigmasViewModel
import com.whatsthegame.Api.ViewModel.GuessEnigmaViewModel
import com.whatsthegame.R
import com.whatsthegame.models.GuessEnigma
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EnigmaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnigmaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var guessEnigmasViewModel: GuessEnigmaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guessEnigmasViewModel = ViewModelProvider(this).get(GuessEnigmaViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var tips: String? = null
    private var answer: GuessEnigma? = null
    private lateinit var enigmasViewModel: EnigmasViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pistas, container, false)

        val pointsCounter = view.findViewById<TextView>(R.id.points)
        var points = 0
        val lifesCounter = view.findViewById<TextView>(R.id.textViewLifes)
        var remainingLives = 3

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

        enigmasViewModel = ViewModelProvider(this).get(EnigmasViewModel::class.java)
        val textViewDifficulty= view.findViewById<TextView>(R.id.difficulty)
        val textViewEnigma = view.findViewById<TextView>(R.id.enigma)
        enigmasViewModel.enigma.observe(viewLifecycleOwner, Observer { enigma ->
            if (enigma != null){
                val wordname = enigma.enigmaName
                answer = GuessEnigma(enigma.answer)
                val gameDifficulty = enigma.difficulty
                tips = enigma.tips
                val colorResId = when (gameDifficulty) {
                    "Fácil" -> R.color.win
                    "Médio" -> R.color.secundaria
                    "Difícil" -> R.color.destaques
                    else -> android.R.color.white
                }
                activity?.runOnUiThread {
                    textViewEnigma.text = "$wordname"
                    textViewDifficulty.text = "$gameDifficulty"

                    val color = ContextCompat.getColor(requireContext(), colorResId)
                    textViewDifficulty.setTextColor(color)
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            enigmasViewModel.fetchEnigmaGame()
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
            val choosedAnswer = GuessEnigma(text.toString())

            if (remainingLives > 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (!text.isNullOrEmpty()) {
                        if (choosedAnswer != answer) {

                            remainingLives--
                            lifesCounter.text = "$remainingLives vidas restantes"

                            if (remainingLives <= 0) {
                                findNavController().navigate(R.id.action_pistasFragment2_to_gameOverMinigamesFragment2)
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

                            guessEnigmasViewModel.guessEnigma(choosedAnswer)
                            points++
                            pointsCounter.text = "$points Pontos"
                            editText.text.clear()
                            tipTextView.text = ""
                            enigmasViewModel.enigma.observe(viewLifecycleOwner, Observer { enigma ->
                                if (enigma != null){
                                    val wordname = enigma.enigmaName
                                    answer = GuessEnigma(enigma.answer)
                                    val gameDifficulty = enigma.difficulty
                                    tips = enigma.tips
                                    val colorResId = when (gameDifficulty) {
                                        "Fácil" -> R.color.win
                                        "Médio" -> R.color.secundaria
                                        "Difícil" -> R.color.destaques
                                        else -> android.R.color.white
                                    }
                                    activity?.runOnUiThread {
                                        textViewEnigma.text = "$wordname"
                                        textViewDifficulty.text = "$gameDifficulty"

                                        val color = ContextCompat.getColor(requireContext(), colorResId)
                                        textViewDifficulty.setTextColor(color)
                                    }
                                }
                            })
                            viewLifecycleOwner.lifecycleScope.launch {
                                enigmasViewModel.fetchEnigmaGame()
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


        return view

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PistasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EnigmaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}