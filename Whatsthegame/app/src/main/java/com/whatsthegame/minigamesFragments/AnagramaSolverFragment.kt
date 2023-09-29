package com.whatsthegame.minigamesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.whatsthegame.Api.ViewModel.AllGamesViewModel
import com.whatsthegame.Api.ViewModel.AnagramsViewModel
import com.whatsthegame.Api.ViewModel.DiaryGameViewModel
import com.whatsthegame.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private var tips: String? = null
    private lateinit var anagramViewModel: AnagramsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_anagrama_solver, container, false)
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

        //val textViewDifficulty = view.findViewById<TextView>(R.id.difficulty)
        val textViewAnagram = view.findViewById<TextView>(R.id.anagram)
        anagramViewModel.anagram.observe(viewLifecycleOwner, Observer { anagram ->
            if (anagram != null){
                val wordname = anagram.wordName
                val answer = anagram.answer
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
                    //textViewDifficulty.text = "$gameDifficulty"
                    //textViewDifficulty.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            anagramViewModel.fetchDiaryGame()
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